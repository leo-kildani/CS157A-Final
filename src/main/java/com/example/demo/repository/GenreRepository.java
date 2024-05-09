package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.demo.entity.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
}
