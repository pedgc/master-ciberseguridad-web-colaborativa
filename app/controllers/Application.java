package controllers;

import models.Constants;
import models.User;
import play.mvc.*;
import play.i18n.Messages;

import java.util.List;

public class Application extends Controller {

    private static void checkTeacher() {
        checkUser();

        User u = (User) renderArgs.get("user");
        if (u.getType().equals(Constants.User.TEACHER)) {
            return;
        } else {
            flash.put("error", Messages.get("Public.login.error.auth"));
            Secure.login();
        }
    }

    private static void checkUser() {
        if (session.contains("username")) {
            User u = User.loadUser(session.get("username"));
            if (u != null) {
                renderArgs.put("user", u);
                return;
            }
        }
        Secure.login();
    }

    public static void index() {
        checkUser();

        User u = (User) renderArgs.get("user");

        if (u.getType().equals(Constants.User.TEACHER)) {
            List<User> students = User.loadStudents();
            render("Application/teacher.html", u, students);
        } else {
            render("Application/student.html", u);
        }
    }

    public static void removeStudent(String student) {
        checkTeacher();

        User.remove(student);
        index();
    }

    public static void setMark(String student) {
        checkTeacher();
        checkUser();
        User u = (User) renderArgs.get("user");
        User stud = User.loadUser(student);
        render(u, stud);
    }

    public static void doSetMark(String student, Integer mark) {
        checkTeacher();
        User u = User.loadUser(student);
        u.setMark(mark);
        u.save();
        index();
    }
}