package com.springboot.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.entity.Students;
import com.springboot.repository.StudentRepository;

@RestController
@RequestMapping("/student")
public class StudentController 
{

    @Autowired
    StudentRepository repo;

    @GetMapping
    public List<Students> getAllStudents()
    {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Students> getStudent(@PathVariable int id) 
    {
        return repo.findById(id)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createStudent(@RequestBody Students s)
    {
        repo.save(s);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Students> updateStudent(@PathVariable int id, @RequestBody Students updatedStudent) {
        return repo.findById(id)
                   .map(existingStudent -> {
                       existingStudent.setName(updatedStudent.getName());
                       existingStudent.setCourse(updatedStudent.getCourse());
                       existingStudent.setAge(updatedStudent.getAge());
                       Students savedStudent = repo.save(existingStudent);
                       return ResponseEntity.ok(savedStudent);
                   })
                   .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> removeStudent(@PathVariable int id)
    {
        if (!repo.existsById(id))
        {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Pagination
    @GetMapping("/{pageNo}/{pageSize}")
    public List<Students> getPaginated(@PathVariable int pageNo, @PathVariable int pageSize) 
    {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Students> pagedResult = repo.findAll(pageable);
        return pagedResult.hasContent() ? pagedResult.getContent() : List.of();
    }

    
    @GetMapping("/sorting")
    public List<Students> getAllByCols(@RequestParam String field1, @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return repo.findAll(Sort.by(sortDirection, field1));
    }

  
    @GetMapping("/{pageNo}/{pageSize}/sorting")
    public List<Students> getPaginatedAndSorted(@PathVariable int pageNo, @PathVariable int pageSize, 
                                                @RequestParam String sortField, @RequestParam(defaultValue = "asc") String sortDir) 
    {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                    Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Students> pagedResult = repo.findAll(pageable);
        return pagedResult.hasContent() ? pagedResult.getContent() : List.of();
    }
}
