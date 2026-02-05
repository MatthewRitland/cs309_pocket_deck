package coms309.course;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Course {

    private String courseName;
    private int capacity;
    private String location;

    public Course (String name, int capacity, String location) {
        courseName = name;
        this.capacity = capacity;
        this.location = location;
    }

    @Override
    public String toString() {
        return courseName + " capacity: " + capacity + " location: " + location;
    }
}
