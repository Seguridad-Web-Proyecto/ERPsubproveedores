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
@Table(name = "compradetalle")
@NamedQueries({
    @NamedQuery(name = "Compradetalle.findAll", query = "SELECT c FROM Compradetalle c"),
    @NamedQuery(name = "Compradetalle.findByCompraid", query = "SELECT c FROM Compradetalle c WHERE c.compraid = :compraid"),
    @NamedQuery(name = "Compradetalle.findByCantidad", query = "SELECT c FROM Compradetalle c WHERE c.cantidad = :cantidad"),
    @NamedQuery(name = "Compradetalle.findByPrecioUnitario", query = "SELECT c FROM Compradetalle c WHERE c.precioUnitario = :precioUnitario"),
    @NamedQuery(name = "Compradetalle.findByImporte", query = "SELECT c FROM Compradetalle c WHERE c.importe = :importe")})
public class Compradetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "compraid")
    private Integer compraid;
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

    public Compradetalle() {
    }

    public Compradetalle(Integer compraid) {
        this.compraid = compraid;
    }

    public Compradetalle(Integer compraid, long cantidad, long precioUnitario, long importe) {
        this.compraid = compraid;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.importe = importe;
    }

    public Integer getCompraid() {
        return compraid;
    }

    public void setCompraid(Integer compraid) {
        this.compraid = compraid;
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
        hash += (compraid != null ? compraid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Compradetalle)) {
            return false;
        }
        Compradetalle other = (Compradetalle) object;
        if ((this.compraid == null && other.compraid != null) || (this.compraid != null && !this.compraid.equals(other.compraid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Compradetalle[ compraid=" + compraid + " ]";
    }
    
}
