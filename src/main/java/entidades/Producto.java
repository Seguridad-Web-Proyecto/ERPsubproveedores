/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jaker
 */
@Entity
@Table(name = "producto")
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p"),
    @NamedQuery(name = "Producto.findByProductoid", query = "SELECT p FROM Producto p WHERE p.productoid = :productoid"),
    @NamedQuery(name = "Producto.findByNombre", query = "SELECT p FROM Producto p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Producto.findByDescripcion", query = "SELECT p FROM Producto p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Producto.findByUnidadMedida", query = "SELECT p FROM Producto p WHERE p.unidadMedida = :unidadMedida"),
    @NamedQuery(name = "Producto.findByPrecioUnitario", query = "SELECT p FROM Producto p WHERE p.precioUnitario = :precioUnitario")})
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "productoid")
    private Integer productoid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "unidad_medida")
    private String unidadMedida;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio_unitario")
    private long precioUnitario;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productoidFk")
    private Collection<Compradetalle> compradetalleCollection;
    @JoinColumn(name = "categoriaid_fk", referencedColumnName = "categoriaid")
    @ManyToOne(optional = false)
    private Categoria categoriaidFk;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productoidFk")
    private Ganancia ganancia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productoidFk")
    private Collection<Ventadetalle> ventadetalleCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productoidFk")
    private Collection<Inventario> inventarioCollection;

    public Producto() {
    }

    public Producto(Integer productoid) {
        this.productoid = productoid;
    }

    public Producto(Integer productoid, String nombre, String descripcion, String unidadMedida, long precioUnitario) {
        this.productoid = productoid;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
    }

    public Integer getProductoid() {
        return productoid;
    }

    public void setProductoid(Integer productoid) {
        this.productoid = productoid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public long getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(long precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Collection<Compradetalle> getCompradetalleCollection() {
        return compradetalleCollection;
    }

    public void setCompradetalleCollection(Collection<Compradetalle> compradetalleCollection) {
        this.compradetalleCollection = compradetalleCollection;
    }

    public Categoria getCategoriaidFk() {
        return categoriaidFk;
    }

    public void setCategoriaidFk(Categoria categoriaidFk) {
        this.categoriaidFk = categoriaidFk;
    }

    public Ganancia getGanancia() {
        return ganancia;
    }

    public void setGanancia(Ganancia ganancia) {
        this.ganancia = ganancia;
    }

    public Collection<Ventadetalle> getVentadetalleCollection() {
        return ventadetalleCollection;
    }

    public void setVentadetalleCollection(Collection<Ventadetalle> ventadetalleCollection) {
        this.ventadetalleCollection = ventadetalleCollection;
    }

    public Collection<Inventario> getInventarioCollection() {
        return inventarioCollection;
    }

    public void setInventarioCollection(Collection<Inventario> inventarioCollection) {
        this.inventarioCollection = inventarioCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productoid != null ? productoid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.productoid == null && other.productoid != null) || (this.productoid != null && !this.productoid.equals(other.productoid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Producto[ productoid=" + productoid + " ]";
    }
    
}
