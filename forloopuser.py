import requests, random, threading,time

def user1(auth):
    
    header={}
    header["Authentication"]= auth
    header["Content-Type"]= "application/json"
    for i in range(0,200):
        start_time=int(time.time()*1000)
        response = requests.get(url="https://reqres.in/api/users",headers=header)   
        # print(response.status_code)
        if(response.status_code==429):
            print(f"After {i} iterations below header was shown")
            print(response.headers)
            break
    end_time=int(time.time()*1000)
    print(start_time)
    print(end_time)
    print(f"time taken {end_time-start_time}")



if __name__=='__main__':
    t1 = threading.Thread(target=user1("8czzkp-q_SUuqxEz8GME"), name='t1')
    t2 = threading.Thread(target=user1("S1S_ZFExkjypryTZwszw"), name='t2') 
    t3 = threading.Thread(target=user1("xBxboHvxSEx95XVcW3nA"), name='t11')

    t1.start()
    t2.start()
    t3.start()
   
    t1.join()
    t2.join()
    t3.join()
  