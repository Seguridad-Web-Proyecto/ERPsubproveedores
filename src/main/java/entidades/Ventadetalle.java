/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author jaker
 */
@Entity
@Table(name = "ventadetalle")
@NamedQueries({
    @NamedQuery(name = "Ventadetalle.findAll", query = "SELECT v FROM Ventadetalle v"),
    @NamedQuery(name = "Ventadetalle.findByVentaid", query = "SELECT v FROM Ventadetalle v WHERE v.ventaid = :ventaid"),
    @NamedQuery(name = "Ventadetalle.findByCantidad", query = "SELECT v FROM Ventadetalle v WHERE v.cantidad = :cantidad"),
    @NamedQuery(name = "Ventadetalle.findByPrecioUnitario", query = "SELECT v FROM Ventadetalle v WHERE v.precioUnitario = :precioUnitario"),
    @NamedQuery(name = "Ventadetalle.findByImporte", query = "SELECT v FROM Ventadetalle v WHERE v.importe = :importe")})
public class Ventadetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ventaid")
    private Integer ventaid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cantidad")
    private long cantidad;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio_unitario")
    private long precioUnitario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "importe")
    private long importe;
    @JoinColumn(name = "productoid_fk", referencedColumnName = "productoid")
    @ManyToOne(optional = false)
    private Producto productoidFk;

    public Ventadetalle() {
    }

    public Ventadetalle(Integer ventaid) {
        this.ventaid = ventaid;
    }

    public Ventadetalle(Integer ventaid, long cantidad, long precioUnitario, long importe) {
        this.ventaid = ventaid;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.importe = importe;
    }

    public Integer getVentaid() {
        return ventaid;
    }

    public void setVentaid(Integer ventaid) {
        this.ventaid = ventaid;
    }

    public long getCantidad() {
        return cantidad;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }

    public long getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(long precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public long getImporte() {
        return importe;
    }

    public void setImporte(long importe) {
        this.importe = importe;
    }

    public Producto getProductoidFk() {
        return productoidFk;
    }

    public void setProductoidFk(Producto productoidFk) {
        this.productoidFk = productoidFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ventaid != null ? ventaid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ventadetalle)) {
            return false;
        }
        Ventadetalle other = (Ventadetalle) object;
        if ((this.ventaid == null && other.ventaid != null) || (this.ventaid != null && !this.ventaid.equals(other.ventaid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Ventadetalle[ ventaid=" + ventaid + " ]";
    }
    
}
