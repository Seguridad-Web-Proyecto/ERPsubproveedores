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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author jaker
 */
@Entity
@Table(name = "ganancia")
@NamedQueries({
    @NamedQuery(name = "Ganancia.findAll", query = "SELECT g FROM Ganancia g"),
    @NamedQuery(name = "Ganancia.findByGananciaid", query = "SELECT g FROM Ganancia g WHERE g.gananciaid = :gananciaid"),
    @NamedQuery(name = "Ganancia.findByPorcentaje", query = "SELECT g FROM Ganancia g WHERE g.porcentaje = :porcentaje")})
public class Ganancia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "gananciaid")
    private Integer gananciaid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje")
    private short porcentaje;
    @JoinColumn(name = "productoid_fk", referencedColumnName = "productoid")
    @OneToOne(optional = false)
    private Producto productoidFk;

    public Ganancia() {
    }

    public Ganancia(Integer gananciaid) {
        this.gananciaid = gananciaid;
    }

    public Ganancia(Integer gananciaid, short porcentaje) {
        this.gananciaid = gananciaid;
        this.porcentaje = porcentaje;
    }

    public Integer getGananciaid() {
        return gananciaid;
    }

    public void setGananciaid(Integer gananciaid) {
        this.gananciaid = gananciaid;
    }

    public short getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(short porcentaje) {
        this.porcentaje = porcentaje;
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
        hash += (gananciaid != null ? gananciaid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ganancia)) {
            return false;
        }
        Ganancia other = (Ganancia) object;
        if ((this.gananciaid == null && other.gananciaid != null) || (this.gananciaid != null && !this.gananciaid.equals(other.gananciaid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Ganancia[ gananciaid=" + gananciaid + " ]";
    }
    
}
