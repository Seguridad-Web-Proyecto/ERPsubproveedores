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

/**
 *
 * @author jaker
 */
@Entity
@Table(name = "historialtrabajo")
@NamedQueries({
    @NamedQuery(name = "Historialtrabajo.findAll", query = "SELECT h FROM Historialtrabajo h"),
    @NamedQuery(name = "Historialtrabajo.findByHistorialid", query = "SELECT h FROM Historialtrabajo h WHERE h.historialid = :historialid"),
    @NamedQuery(name = "Historialtrabajo.findByFechaInicio", query = "SELECT h FROM Historialtrabajo h WHERE h.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Historialtrabajo.findByFechaFin", query = "SELECT h FROM Historialtrabajo h WHERE h.fechaFin = :fechaFin")})
public class Historialtrabajo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "historialid")
    private Integer historialid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @JoinColumn(name = "cargoid_fk", referencedColumnName = "cargoid")
    @ManyToOne(optional = false)
    private Cargo cargoidFk;
    @JoinColumn(name = "empleadoid_fk", referencedColumnName = "empleadoid")
    @ManyToOne(optional = false)
    private Empleado empleadoidFk;

    public Historialtrabajo() {
    }

    public Historialtrabajo(Integer historialid) {
        this.historialid = historialid;
    }

    public Historialtrabajo(Integer historialid, Date fechaInicio) {
        this.historialid = historialid;
        this.fechaInicio = fechaInicio;
    }

    public Integer getHistorialid() {
        return historialid;
    }

    public void setHistorialid(Integer historialid) {
        this.historialid = historialid;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Cargo getCargoidFk() {
        return cargoidFk;
    }

    public void setCargoidFk(Cargo cargoidFk) {
        this.cargoidFk = cargoidFk;
    }

    public Empleado getEmpleadoidFk() {
        return empleadoidFk;
    }

    public void setEmpleadoidFk(Empleado empleadoidFk) {
        this.empleadoidFk = empleadoidFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (historialid != null ? historialid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Historialtrabajo)) {
            return false;
        }
        Historialtrabajo other = (Historialtrabajo) object;
        if ((this.historialid == null && other.historialid != null) || (this.historialid != null && !this.historialid.equals(other.historialid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Historialtrabajo[ historialid=" + historialid + " ]";
    }
    
}
