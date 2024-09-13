package com.sistem.sistema.entity;


import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductosEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productoId")
    Long productoId;

    @Basic
    @Column(name = "nombre", unique = true)
    String nombre;

    @Basic
    @Column(name = "precio")
    Double precio;

    @Basic
    @Column(name = "descripcion")
    String descripcion;

    @Basic
    @Column(name = "imagen")
    String imagen;

    @Basic
    @Column(name = "fecha")
    Timestamp fecha;

    @JsonIgnoreProperties({"productos", "handler", "hibernateLazyInitializer"})
    @OneToOne()
    @JoinTable(
        name = "productos_categorias",
        joinColumns = @JoinColumn(name = "productoId"),
        inverseJoinColumns = @JoinColumn(name = "categoriaId"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"productoId", "categoriaId"})}
    )
    private CategoriasEntity categorias;
}
