package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Royaume.
 */
@Entity
@Table(name = "royaume")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Royaume implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "regles")
    private String regles;

    @Lob
    @Column(name = "arriereplan")
    private byte[] arriereplan;

    @Column(name = "arriereplan_content_type")
    private String arriereplanContentType;

    @Column(name = "is_public")
    private Boolean isPublic;

    @ManyToOne
    @JsonIgnoreProperties(value = { "lien", "maison", "lienOrigine", "lienCible" }, allowSetters = true)
    private Ville ville;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Royaume id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Royaume nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return this.description;
    }

    public Royaume description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegles() {
        return this.regles;
    }

    public Royaume regles(String regles) {
        this.setRegles(regles);
        return this;
    }

    public void setRegles(String regles) {
        this.regles = regles;
    }

    public byte[] getArriereplan() {
        return this.arriereplan;
    }

    public Royaume arriereplan(byte[] arriereplan) {
        this.setArriereplan(arriereplan);
        return this;
    }

    public void setArriereplan(byte[] arriereplan) {
        this.arriereplan = arriereplan;
    }

    public String getArriereplanContentType() {
        return this.arriereplanContentType;
    }

    public Royaume arriereplanContentType(String arriereplanContentType) {
        this.arriereplanContentType = arriereplanContentType;
        return this;
    }

    public void setArriereplanContentType(String arriereplanContentType) {
        this.arriereplanContentType = arriereplanContentType;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public Royaume isPublic(Boolean isPublic) {
        this.setIsPublic(isPublic);
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Ville getVille() {
        return this.ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public Royaume ville(Ville ville) {
        this.setVille(ville);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Royaume)) {
            return false;
        }
        return id != null && id.equals(((Royaume) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Royaume{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", regles='" + getRegles() + "'" +
            ", arriereplan='" + getArriereplan() + "'" +
            ", arriereplanContentType='" + getArriereplanContentType() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            "}";
    }
}
