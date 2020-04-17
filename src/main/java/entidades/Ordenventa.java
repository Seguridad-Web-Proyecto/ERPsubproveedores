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
@Table(name = "ordenventa")
@NamedQueries({
    @NamedQuery(name = "Ordenventa.findAll", query = "SELECT o FROM Ordenventa o"),
    @NamedQuery(name = "Ordenventa.findByOrdenventaid", query = "SELECT o FROM Ordenventa o WHERE o.ordenventaid = :ordenventaid"),
    @NamedQuery(name = "Ordenventa.findByFechaVenta", query = "SELECT o FROM Ordenventa o WHERE o.fechaVenta = :fechaVenta"),
    @NamedQuery(name = "Ordenventa.findByStatus", query = "SELECT o FROM Ordenventa o WHERE o.status = :status"),
    @NamedQuery(name = "Ordenventa.findByIva", query = "SELECT o FROM Ordenventa o WHERE o.iva = :iva"),
    @NamedQuery(name = "Ordenventa.findBySubtotal", query = "SELECT o FROM Ordenventa o WHERE o.subtotal = :subtotal"),
    @NamedQuery(name = "Ordenventa.findByTotal", query = "SELECT o FROM Ordenventa o WHERE o.total = :total"),
    @NamedQuery(name = "Ordenventa.findByDescripcion", query = "SELECT o FROM Ordenventa o WHERE o.descripcion = :descripcion")})
public class Ordenventa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ordenventaid")
    private Integer ordenventaid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_venta")
    @Temporal(TemporalType.DATE)
    private Date fechaVenta;
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
    @JoinColumn(name = "clienteid_fk", referencedColumnName = "clienteid")
    @ManyToOne(optional = false)
    private Cliente clienteidFk;
    @JoinColumn(name = "facturaid_fk", referencedColumnName = "facturaventaid")
    @ManyToOne(optional = false)
    private Facturaventa facturaidFk;

    public Ordenventa() {
    }

    public Ordenventa(Integer ordenventaid) {
        this.ordenventaid = ordenventaid;
    }

    public Ordenventa(Integer ordenventaid, Date fechaVenta, String status, short iva, long subtotal, long total, String descripcion) {
        this.ordenventaid = ordenventaid;
        this.fechaVenta = fechaVenta;
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

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
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

    public Cliente getClienteidFk() {
        return clienteidFk;
    }

    public void setClienteidFk(Cliente clienteidFk) {
        this.clienteidFk = clienteidFk;
    }

    public Facturaventa getFacturaidFk() {
        return facturaidFk;
    }

    public void setFacturaidFk(Facturaventa facturaidFk) {
        this.facturaidFk = facturaidFk;
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
        if (!(object instanceof Ordenventa)) {
            return false;
        }
        Ordenventa other = (Ordenventa) object;
        if ((this.ordenventaid == null && other.ordenventaid != null) || (this.ordenventaid != null && !this.ordenventaid.equals(other.ordenventaid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Ordenventa[ ordenventaid=" + ordenventaid + " ]";
    }
    
}
