package com.example.modernworkshopapp.Model;

public class AdminOrders {
    private String date, email, image, name, phone, quantity, status, time;

    public AdminOrders() {

    }

    public AdminOrders(String date, String email, String image, String name, String phone, String quantity, String status, String time) {
        this.date = date;
        this.email = email;
        this.image = image;
        this.name = name;
        this.phone = phone;
        this.quantity = quantity;
        this.status = status;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
