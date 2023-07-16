import time
import sched
import stock_predictor
import OperateDB
import prob_predictor_with_sse
import prob_predictor_allinone

num_days = 10

# 定义一个函数，用于每24hrs运行一次
# 创建一个调度器对象
scheduler = sched.scheduler(time.time, time.sleep)

# 定义一个函数，用于在24hrs后再次运行
def run_scheduler():
    scheduler.enter(86400, 1, run_scheduler)
    ticker_list = OperateDB.GetCompanyList()
    for ticker in ticker_list:    
        # exact_price_predictor
        output_df=stock_predictor.Entry(ticker, num_days)
        OperateDB.InsertPredictResult(output_df)
        
        # # probability predictor with sse composite index
        # output_df=prob_predictor_with_sse.Entry(ticker)
        # OperateDB.InsertProbPredictResult(output_df)

    # # probability predictor all in one
    # output_df=prob_predictor_allinone.Entry(ticker_list)
    # OperateDB.InsertProbPredictResult(output_df)
    

# 第一次运行程序
scheduler.enter(0, 1, run_scheduler)

# 启动调度器
scheduler.run()

