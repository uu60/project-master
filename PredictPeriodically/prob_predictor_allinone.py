import os
import pandas as pd
import numpy as np
from pandas_datareader import data
from datetime import datetime
from sklearn.preprocessing import MinMaxScaler
from keras.preprocessing.sequence import TimeseriesGenerator
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
from tensorflow.keras.layers import LSTM
import yfinance as yf
import matplotlib.pyplot as plt
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, LSTM
from tensorflow.keras.callbacks import EarlyStopping
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.regularizers import l2
from tensorflow.keras.layers import Dropout
import matplotlib.dates as mdates
import time
from datetime import datetime, timedelta

start_time = time.time()

def prob_predictor(ticker_list, attri):
    # Reading stock data from yahoo
    yf.pdr_override()
    df = data.get_data_yahoo(tickers=ticker_list[0], start='2015-01-01', end=datetime.now()) [attri] [-1500:]
    temp_df = df
    counter = 0
    for i in range(1,len(ticker_list)):
        cur_df = data.get_data_yahoo(tickers=ticker_list[i], start='2015-01-01', end=datetime.now()) [attri] [-1500:]
        if len(cur_df) == 1500:
            for j in range(len(df)):
                temp_df[j] += cur_df[j]
            counter += 1
    for j in range(len(df)):
        temp_df[j] = temp_df[j]/(len(ticker_list)-counter)
    df = temp_df
    print(df)

    # Label the data 1 for rise and 0 for fall
    df_binary = pd.DataFrame(index=df.index)
    df_binary['price'] = df
    df_binary['change'] = df_binary['price'].diff()
    df_binary['up'] = np.where(df_binary['change'] > 0, 1, 0)
    df_binary = df_binary[1:]

    # Scale the data
    scaler = MinMaxScaler(feature_range=(0, 1))
    df_binary['price'] = scaler.fit_transform(df_binary['price'].values.reshape(-1, 1))

    # Split into training and testing data
    training_size = int(len(df_binary) * 0.65)
    train_df, test_df = df_binary[:training_size], df_binary[training_size:]

    # Preparing the dataset
    train = TimeseriesGenerator(train_df['price'].values, train_df['up'].values, length=100, batch_size=64)
    test = TimeseriesGenerator(test_df['price'].values, test_df['up'].values, length=100, batch_size=64)
    X_train, y_train = train[0][0], train[0][1]
    X_test, y_test = test[0][0], test[0][1]
    X_train = np.reshape(X_train, (X_train.shape[0], X_train.shape[1], 1))
    X_test = np.reshape(X_test, (X_test.shape[0], X_test.shape[1], 1))

    # Define LSTM network
    model = Sequential()
    model.add(LSTM(50, return_sequences=True, input_shape=(100, 1), dropout=0.2, recurrent_dropout=0.2))
    model.add(LSTM(50, dropout=0.2, recurrent_dropout=0.2))
    model.add(Dense(25, kernel_regularizer=l2(0.01), bias_regularizer=l2(0.01)))
    model.add(Dropout(0.5))
    model.add(Dense(1, activation='relu'))  

    opt = Adam(learning_rate=1e-2)
    model.compile(optimizer=opt, loss='binary_crossentropy', metrics=['accuracy'])

    # Early stopping
    es = EarlyStopping(monitor='val_loss', mode='min', verbose=1, patience=50)

    # Fit the model
    history = model.fit(
        train,
        validation_data=test,
        steps_per_epoch=len(train),
        validation_steps=len(test),
        epochs=150,
        callbacks=[es],
        verbose=0
    )

    return model

# Make prediction on given company and attribute
def make_prediction(model, company_name, attri):
    yf.pdr_override()
    df = data.get_data_yahoo(tickers=company_name, start='2015-01-01', end=datetime.now()) [attri] [-1500:]

    df_binary = pd.DataFrame(index=df.index)
    df_binary['price'] = df
    df_binary['change'] = df_binary['price'].diff()
    df_binary['up'] = np.where(df_binary['change'] > 0, 1, 0)
    df_binary = df_binary[1:]

    scaler = MinMaxScaler(feature_range=(0, 1))
    df_binary['price'] = scaler.fit_transform(df_binary['price'].values.reshape(-1, 1))

    last_100_days = np.reshape(df_binary['price'].values[-100:], (1, 100, 1))
    prediction = model.predict(last_100_days)
    result = prediction[0][0]

    return result

def Entry(ticker_list):

    predicted_attribute_list = ['Open', 'High', 'Low', 'Close', 'Volume']
    output_df = pd.DataFrame(columns=['code', 'field', 'up', 'time', 'correct'])

    for attri in predicted_attribute_list:
        model = prob_predictor(ticker_list, attri)
        for ticker in ticker_list:
            up = make_prediction(model, ticker, attri)
            list_row = [ticker, attri.lower(), up, datetime.now().date() + timedelta(days=1), -1]
            output_df.loc[len(output_df)] = list_row
        print(output_df)

    # Save it as a csv file
    os.makedirs("csv_prob", exist_ok=True)
    output_df.to_csv(f"csv_prob/prob_table_{datetime.now().date() + timedelta(days=1)}.csv", sep='\t', encoding='utf-8')
    print('csv file saved!\n')

    # Print out the execution time
    print("------------------ %s seconds ------------------" % round((time.time() - start_time), 2))
    return output_df
