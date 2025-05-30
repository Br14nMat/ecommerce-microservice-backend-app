from locust import HttpUser, task, between
import random

class OrderService(HttpUser):
    wait_time = between(1, 3)

    @task(2)
    def get_all_orders(self):
        self.client.get("/api/orders")

    @task(1)
    def create_cart(self):
        cart_data = {
            "cartId": 1,
            "userId": 1,
            "user": {
                "userId": 1,
                "firstName": "dassadsda",
                "lastName": "adsdas",
                "imageUrl": "imgurl",
                "email": "brian@gmail.com",
                "phone": "3223435345534"
            }
        }
        self.client.post("/api/carts", json=cart_data)
