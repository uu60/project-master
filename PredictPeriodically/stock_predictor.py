import os

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
import pandas as pd
import numpy as np
from pandas_datareader import data
from pylab import plt
import yfinance as yf
from datetime import datetime, timedelta
from sklearn.preprocessing import MinMaxScaler
from keras.preprocessing.sequence import TimeseriesGenerator
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
from tensorflow.keras.layers import LSTM
import matplotlib.pyplot as plt
import pandas as pd
import matplotlib.dates as mdates
import time

start_time = time.time()

def one_attr_predictor(company_name, attribute_to_be_predicted, num_days):
    # Reading stock data from yahoo
    yf.pdr_override()
    df = data.get_data_yahoo(tickers=company_name, start='2015-01-01', end=datetime.now())
    # print(df)
    df = df[attribute_to_be_predicted]
    df = df[-1500:]
    trading_dates = df.index.to_pydatetime()
    # plt.plot(df)
    # plt.show()
    # print(df)

    # Scale the data
    scaler = MinMaxScaler(feature_range=(0, 1))
    rdata = scaler.fit_transform(np.array(df).reshape(-1, 1))
    training_size = int(len(rdata) * 0.65)
    train_period, test_period = rdata[:training_size], rdata[training_size:]
    np.array(df).reshape(-1, 1)

    # Preparing the dataset
    train = TimeseriesGenerator(train_period, train_period, length=100, batch_size=1000)
    test = TimeseriesGenerator(test_period, test_period, length=100, batch_size=1000)
    X_train, y_train = list(train)[0][0], list(train)[0][1]
    X_test, y_test = list(test)[0][0], list(test)[0][1]

    # Build the LSTM model
    model = Sequential()
    model.add(LSTM(128, return_sequences=True, input_shape=(100, 1)))
    model.add(LSTM(64, return_sequences=False))
    model.add(Dense(25))
    model.add(Dense(1))
    model.compile(loss='mean_squared_error', optimizer='adam')
    # model.summary()
    print(f'Training model for predicting {attribute_to_be_predicted} of {company_name}:')
    history = model.fit(X_train, y_train, validation_data=(X_test, y_test), epochs=100, batch_size=64, verbose=True)

    # # Plot the loss line graph
    # loss = history.history['loss']
    # val_loss = history.history['val_loss']
    # epochs = range(1, len(loss) + 1)
    # plt.plot(epochs, loss, 'bo', label='Training loss')
    # plt.plot(epochs, val_loss, 'b', label='Validation loss')
    # plt.title('Training and validation loss')
    # plt.legend()
    # plt.show()

    # Evaluate the model using testing dataset and use it to predict future values
    train_predict = scaler.inverse_transform(model.predict(X_train, verbose=0))
    test_predict = scaler.inverse_transform(model.predict(X_test, verbose=0))
    look_back = 100
    recent_data = np.array(df[-look_back:]).reshape(-1, 1)
    recent_data_scaled = scaler.transform(recent_data)
    predictions = []
    for _ in range(num_days):
        recent_data_scaled = recent_data_scaled.reshape((1, look_back, 1))
        predicted_price = model.predict(recent_data_scaled, verbose=0)
        predictions.append(scaler.inverse_transform(predicted_price))
        recent_data_scaled = np.append(recent_data_scaled[:, 1:, :], predicted_price.reshape((1, 1, 1)), axis=1)
    testPredictPlot = np.empty_like(rdata)
    testPredictPlot[:, :] = np.nan
    testPredictPlot[len(train_predict) + (look_back * 2):len(rdata)] = test_predict
    last_date = df.index[-1]
    future_dates = pd.date_range(start=last_date, periods=num_days + 1).to_pydatetime()[1:]
    predictions_flat = np.array(predictions).reshape(-1, )

    # Plot the final line graph
    plt.figure(figsize=(12, 8))
    plt.plot(df.index, scaler.inverse_transform(rdata))
    plt.plot(df.index[len(train_predict) + (look_back * 2):], test_predict)
    plt.plot(future_dates, predictions_flat, 'r')
    plt.legend(['Original', 'Testing', 'Predictions'], loc='upper right')
    plt.xlabel('Date')
    plt.ylabel(attribute_to_be_predicted)
    plt.title(f"{attribute_to_be_predicted} of {company_name}")
    os.makedirs("figures", exist_ok=True)
    plt.savefig(f"figures/{company_name}_{attribute_to_be_predicted}_{num_days}days")
    print('Line graph saved!\n')
    # plt.show()

    return predictions_flat


def Entry(company_name, num_days):
    #####################################################################################################################

    # company_name = input('Please enter a ticker symbol:')
    # num_days = int(input('Please enter the number of days you would like to make your prediction:'))

    # Construction of the output dataframe
    predicted_attribute_list = ['Open', 'High', 'Low', 'Close', 'Volume']
    date_column = []
    for i in range(0, num_days):
        date_column.append(datetime.now().date() + timedelta(days=i))
    output_data = {'code': [company_name] * num_days,
                   'time': date_column,
                   'daily': [2] * num_days}
    output_df = pd.DataFrame(output_data)

    for attri in predicted_attribute_list:
        prediction_column = one_attr_predictor(company_name, attri, num_days)
        output_df[attri.lower()] = prediction_column

    output_df['dividends'] = [0.00000] * num_days
    output_df['stock_splits'] = [0.00000] * num_days
    output_df = output_df.astype({'volume': 'int'})
    print(output_df)

    # Save it as a csv file
    os.makedirs("csv", exist_ok=True)
    output_df.to_csv(f"csv/{company_name}_{num_days}days_table.csv", sep='\t', encoding='utf-8')
    print('csv file saved!\n')

    # Print out the execution time
    print("------------------ %s seconds ------------------" % round((time.time() - start_time), 2))
    return output_df
