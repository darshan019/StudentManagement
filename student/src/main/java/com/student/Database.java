package com.student;

import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Data {
    int id;
    String name;
    String course;
    double gpa;

    public Data(int id, String name, String course, double gpa) {
        this.id = id;
        this.course = course;
        this.gpa = gpa;
        this.name = name;
    }

    public String toString() {
        return "ID: " + id + "\nName: " + name + "\nCourse: " + course + "\nGPA: " + gpa + "\n\n";
    }
}

public class Database {
    Dotenv dotenv = Dotenv.load();
    private String url = dotenv.get("URL");
    private String user = dotenv.get("USER");
    private String password = dotenv.get("PASSWORD");

    private Connection conn = null;

    public Connection getConn() {
        return conn;
    }

    public List<Data> RetreiveData(String query) {
        List<Data> list = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(url, user, password);

            if (conn != null) {
                System.out.println("Connection successful");

                PreparedStatement psmt = conn.prepareStatement(query);
                ResultSet rs = psmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String course = rs.getString("course");
                    double gpa = rs.getDouble("gpa");
                    list.add(new Data(id, name, course, gpa));
                }

                rs.close();
                psmt.close();
                System.out.println(rs);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
