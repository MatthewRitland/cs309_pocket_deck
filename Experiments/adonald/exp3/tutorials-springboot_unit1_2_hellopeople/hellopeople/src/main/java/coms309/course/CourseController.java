package coms309.course;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class CourseController {


    HashMap<String, Course> courseList = new HashMap<>();


    @PostMapping("/course")
    public String createCourse (@RequestBody Course course) {
        courseList.put(course.getCourseName(), course);
        return "New course " + course.getCourseName() + " saved.";
    }

    @GetMapping("/course")
    public HashMap<String, Course> getAllCourses () {
        return courseList;
    }

    @GetMapping("/course/{courseName}")
    public Course getCourse (@PathVariable String courseName) {
        return courseList.get(courseName);
    }

    @DeleteMapping("/course/{courseName}")
    public HashMap<String, Course> deleteCourse (@PathVariable String courseName) {
        courseList.remove(courseName);
        return courseList;
    }

    @PutMapping("/course/{courseName}")
    public Course updateCourse (@PathVariable String courseName, @RequestBody Course course) {
        courseList.replace(courseName, course);
        return courseList.get(courseName);
    }

}