package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Lien.
 */
@Entity
@Table(name = "lien")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Lien implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @Lob
    @Column(name = "icone")
    private byte[] icone;

    @Column(name = "icone_content_type")
    private String iconeContentType;

    @Column(name = "absisce")
    private Integer absisce;

    @Column(name = "ordonnee")
    private Integer ordonnee;

    @Lob
    @Column(name = "arriereplan")
    private byte[] arriereplan;

    @Column(name = "arriereplan_content_type")
    private String arriereplanContentType;

    @JsonIgnoreProperties(value = { "lien", "maison", "lienOrigine", "lienCible" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Ville villeOrigine;

    @JsonIgnoreProperties(value = { "lien", "maison", "lienOrigine", "lienCible" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Ville villeCible;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lien id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Lien nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public byte[] getIcone() {
        return this.icone;
    }

    public Lien icone(byte[] icone) {
        this.setIcone(icone);
        return this;
    }

    public void setIcone(byte[] icone) {
        this.icone = icone;
    }

    public String getIconeContentType() {
        return this.iconeContentType;
    }

    public Lien iconeContentType(String iconeContentType) {
        this.iconeContentType = iconeContentType;
        return this;
    }

    public void setIconeContentType(String iconeContentType) {
        this.iconeContentType = iconeContentType;
    }

    public Integer getAbsisce() {
        return this.absisce;
    }

    public Lien absisce(Integer absisce) {
        this.setAbsisce(absisce);
        return this;
    }

    public void setAbsisce(Integer absisce) {
        this.absisce = absisce;
    }

    public Integer getOrdonnee() {
        return this.ordonnee;
    }

    public Lien ordonnee(Integer ordonnee) {
        this.setOrdonnee(ordonnee);
        return this;
    }

    public void setOrdonnee(Integer ordonnee) {
        this.ordonnee = ordonnee;
    }

    public byte[] getArriereplan() {
        return this.arriereplan;
    }

    public Lien arriereplan(byte[] arriereplan) {
        this.setArriereplan(arriereplan);
        return this;
    }

    public void setArriereplan(byte[] arriereplan) {
        this.arriereplan = arriereplan;
    }

    public String getArriereplanContentType() {
        return this.arriereplanContentType;
    }

    public Lien arriereplanContentType(String arriereplanContentType) {
        this.arriereplanContentType = arriereplanContentType;
        return this;
    }

    public void setArriereplanContentType(String arriereplanContentType) {
        this.arriereplanContentType = arriereplanContentType;
    }

    public Ville getVilleOrigine() {
        return this.villeOrigine;
    }

    public void setVilleOrigine(Ville ville) {
        this.villeOrigine = ville;
    }

    public Lien villeOrigine(Ville ville) {
        this.setVilleOrigine(ville);
        return this;
    }

    public Ville getVilleCible() {
        return this.villeCible;
    }

    public void setVilleCible(Ville ville) {
        this.villeCible = ville;
    }

    public Lien villeCible(Ville ville) {
        this.setVilleCible(ville);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lien)) {
            return false;
        }
        return id != null && id.equals(((Lien) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lien{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", icone='" + getIcone() + "'" +
            ", iconeContentType='" + getIconeContentType() + "'" +
            ", absisce=" + getAbsisce() +
            ", ordonnee=" + getOrdonnee() +
            ", arriereplan='" + getArriereplan() + "'" +
            ", arriereplanContentType='" + getArriereplanContentType() + "'" +
            "}";
    }
}
