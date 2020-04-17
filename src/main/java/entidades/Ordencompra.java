/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jaker
 */
@Entity
@Table(name = "ordencompra")
@NamedQueries({
    @NamedQuery(name = "Ordencompra.findAll", query = "SELECT o FROM Ordencompra o"),
    @NamedQuery(name = "Ordencompra.findByOrdenventaid", query = "SELECT o FROM Ordencompra o WHERE o.ordenventaid = :ordenventaid"),
    @NamedQuery(name = "Ordencompra.findByFechaCompra", query = "SELECT o FROM Ordencompra o WHERE o.fechaCompra = :fechaCompra"),
    @NamedQuery(name = "Ordencompra.findByStatus", query = "SELECT o FROM Ordencompra o WHERE o.status = :status"),
    @NamedQuery(name = "Ordencompra.findByIva", query = "SELECT o FROM Ordencompra o WHERE o.iva = :iva"),
    @NamedQuery(name = "Ordencompra.findBySubtotal", query = "SELECT o FROM Ordencompra o WHERE o.subtotal = :subtotal"),
    @NamedQuery(name = "Ordencompra.findByTotal", query = "SELECT o FROM Ordencompra o WHERE o.total = :total"),
    @NamedQuery(name = "Ordencompra.findByDescripcion", query = "SELECT o FROM Ordencompra o WHERE o.descripcion = :descripcion")})
public class Ordencompra implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ordenventaid")
    private Integer ordenventaid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_compra")
    @Temporal(TemporalType.DATE)
    private Date fechaCompra;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "iva")
    private short iva;
    @Basic(optional = false)
    @NotNull
    @Column(name = "subtotal")
    private long subtotal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total")
    private long total;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "facturaid_fk", referencedColumnName = "facturaventaid")
    @ManyToOne(optional = false)
    private Facturaventa facturaidFk;
    @JoinColumn(name = "proveedorid_fk", referencedColumnName = "proveedorid")
    @ManyToOne(optional = false)
    private Proveedor proveedoridFk;

    public Ordencompra() {
    }

    public Ordencompra(Integer ordenventaid) {
        this.ordenventaid = ordenventaid;
    }

    public Ordencompra(Integer ordenventaid, Date fechaCompra, String status, short iva, long subtotal, long total, String descripcion) {
        this.ordenventaid = ordenventaid;
        this.fechaCompra = fechaCompra;
        this.status = status;
        this.iva = iva;
        this.subtotal = subtotal;
        this.total = total;
        this.descripcion = descripcion;
    }

    public Integer getOrdenventaid() {
        return ordenventaid;
    }

    public void setOrdenventaid(Integer ordenventaid) {
        this.ordenventaid = ordenventaid;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public short getIva() {
        return iva;
    }

    public void setIva(short iva) {
        this.iva = iva;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(long subtotal) {
        this.subtotal = subtotal;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Facturaventa getFacturaidFk() {
        return facturaidFk;
    }

    public void setFacturaidFk(Facturaventa facturaidFk) {
        this.facturaidFk = facturaidFk;
    }

    public Proveedor getProveedoridFk() {
        return proveedoridFk;
    }

    public void setProveedoridFk(Proveedor proveedoridFk) {
        this.proveedoridFk = proveedoridFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ordenventaid != null ? ordenventaid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ordencompra)) {
            return false;
        }
        Ordencompra other = (Ordencompra) object;
        if ((this.ordenventaid == null && other.ordenventaid != null) || (this.ordenventaid != null && !this.ordenventaid.equals(other.ordenventaid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Ordencompra[ ordenventaid=" + ordenventaid + " ]";
    }
    
}
