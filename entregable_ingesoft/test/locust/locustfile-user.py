from locust import HttpUser, task, between
import random

class UserService(HttpUser):
    wait_time = between(1, 3)

    @task(2)
    def get_all_users(self):
        self.client.get("/api/users")

    @task(2)
    def get_all_address(self):
        self.client.get("/api/address")

    @task(2)
    def get_all_credentials(self):
        self.client.get("/api/credentials")

    @task(2)
    def get_all_tokens(self):
        self.client.get("/api/verificationTokens")
    