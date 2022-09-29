import json
from faker import Faker
from locust import HttpLocust, SequentialTaskSet, constant, HttpUser, run_single_user, tag, task
class DummyGetApiTest(SequentialTaskSet):


    def __init__(self, parent): 
        super().__init__(parent)
     
        
    @task
    @tag("users")    
    def getuserdetails(self):
        header={}
        header["Authentication"]= "8czzkp-q_SUuqxEz8GME"
        header["Content-Type"]= "application/json"
        with self.client.get("/users", catch_response=True, name="Fetch User Details",headers=header) as response:
            print(response.headers)
            print(response.status_code)
            if response.status_code==200:
                response.success()
            else:
                response.failure(
                    f'Failure is caused by {response.status_code} {response.text}')
   
  

class DummyPostApiTest(SequentialTaskSet):

    def __init__(self, parent): 
        super().__init__(parent)
        self.fake = Faker()
        self.jsonbody={
            "name":"joy"+str(self.fake.random_number(12,True)) ,
             "job": "Engineer"
        }

    @task
    @tag("users")
    def createuser(self):
        header={}
        header["Authentication"]= "8czzkp-q_SUuqxEz8GME"
        header["Content-Type"]= "application/json"
        with self.client.post("/users", json=self.jsonbody,catch_response=True, name="Create user",headers=header) as response:
            print(self.jsonbody)
            if response.status_code==200:
                response.success()
            else:
                response.failure(response.text)

    @task
    def stop(self):
        self.interrupt()

class LoadTest(HttpUser):
    host = "https://reqres.in/api"
    wait_time = constant(1)
    tasks = [DummyGetApiTest,DummyPostApiTest]