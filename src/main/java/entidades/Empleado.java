/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author jaker
 */
@Entity
@Table(name = "empleado")
@NamedQueries({
    @NamedQuery(name = "Empleado.findAll", query = "SELECT e FROM Empleado e"),
    @NamedQuery(name = "Empleado.findByEmpleadoid", query = "SELECT e FROM Empleado e WHERE e.empleadoid = :empleadoid"),
    @NamedQuery(name = "Empleado.findByFechaVinculo", query = "SELECT e FROM Empleado e WHERE e.fechaVinculo = :fechaVinculo"),
    @NamedQuery(name = "Empleado.findByFechaRetiro", query = "SELECT e FROM Empleado e WHERE e.fechaRetiro = :fechaRetiro")})
public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "empleadoid")
    private Integer empleadoid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_vinculo")
    @Temporal(TemporalType.DATE)
    private Date fechaVinculo;
    @Column(name = "fecha_retiro")
    @Temporal(TemporalType.DATE)
    private Date fechaRetiro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empleadoidFk")
    private Collection<Historialtrabajo> historialtrabajoCollection;
    @JoinColumn(name = "usuarioid_fk", referencedColumnName = "usuarioid")
    @ManyToOne(optional = false)
    private Usuario usuarioidFk;

    public Empleado() {
    }

    public Empleado(Integer empleadoid) {
        this.empleadoid = empleadoid;
    }

    public Empleado(Integer empleadoid, Date fechaVinculo) {
        this.empleadoid = empleadoid;
        this.fechaVinculo = fechaVinculo;
    }

    public Integer getEmpleadoid() {
        return empleadoid;
    }

    public void setEmpleadoid(Integer empleadoid) {
        this.empleadoid = empleadoid;
    }

    public Date getFechaVinculo() {
        return fechaVinculo;
    }

    public void setFechaVinculo(Date fechaVinculo) {
        this.fechaVinculo = fechaVinculo;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public Collection<Historialtrabajo> getHistorialtrabajoCollection() {
        return historialtrabajoCollection;
    }

    public void setHistorialtrabajoCollection(Collection<Historialtrabajo> historialtrabajoCollection) {
        this.historialtrabajoCollection = historialtrabajoCollection;
    }

    public Usuario getUsuarioidFk() {
        return usuarioidFk;
    }

    public void setUsuarioidFk(Usuario usuarioidFk) {
        this.usuarioidFk = usuarioidFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (empleadoid != null ? empleadoid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empleado)) {
            return false;
        }
        Empleado other = (Empleado) object;
        if ((this.empleadoid == null && other.empleadoid != null) || (this.empleadoid != null && !this.empleadoid.equals(other.empleadoid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Empleado[ empleadoid=" + empleadoid + " ]";
    }
    
}
