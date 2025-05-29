from locust import HttpUser, task, between
import random

class ProductUser(HttpUser):
    wait_time = between(1, 3)

    @task(2)
    def get_all_products(self):
        self.client.get("/api/products")

    @task(1)
    def create_product(self):
        product_data = {
            "productTitle": "Camisa deportiva",
            "imageUrl": "camisa.jpg",
            "sku": f"SKU-DEP-{random.randint(1000,9999)}",
            "priceUnit": 19.99,
            "quantity": 120,
            "category": {
                "categoryId": 1
            }
        }
        self.client.post("/api/products", json=product_data)

    @task(1)
    def update_product(self):
        product_data = {
            "productTitle": "Camisa deportiva actualizada",
            "imageUrl": "camisa.jpg",
            "sku": "SKU-DEP-CM1",
            "priceUnit": 24.99,
            "quantity": 110,
            "category": {
                "categoryId": 1
            }
        }

        self.client.put("/api/products/1", json=product_data)
