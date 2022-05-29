package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ApplicationUser.
 */
@Entity
@Table(name = "application_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApplicationUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "photoprincipal")
    private byte[] photoprincipal;

    @Column(name = "photoprincipal_content_type")
    private String photoprincipalContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApplicationUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPhotoprincipal() {
        return this.photoprincipal;
    }

    public ApplicationUser photoprincipal(byte[] photoprincipal) {
        this.setPhotoprincipal(photoprincipal);
        return this;
    }

    public void setPhotoprincipal(byte[] photoprincipal) {
        this.photoprincipal = photoprincipal;
    }

    public String getPhotoprincipalContentType() {
        return this.photoprincipalContentType;
    }

    public ApplicationUser photoprincipalContentType(String photoprincipalContentType) {
        this.photoprincipalContentType = photoprincipalContentType;
        return this;
    }

    public void setPhotoprincipalContentType(String photoprincipalContentType) {
        this.photoprincipalContentType = photoprincipalContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationUser)) {
            return false;
        }
        return id != null && id.equals(((ApplicationUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationUser{" +
            "id=" + getId() +
            ", photoprincipal='" + getPhotoprincipal() + "'" +
            ", photoprincipalContentType='" + getPhotoprincipalContentType() + "'" +
            "}";
    }
}
