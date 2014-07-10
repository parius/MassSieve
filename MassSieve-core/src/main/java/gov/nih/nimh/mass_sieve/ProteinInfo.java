/*
 * ProteinInfo.java
 *
 * Created on July 26, 2007, 11:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.nih.nimh.mass_sieve;

import java.io.Serializable;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.ProteinTools;
import org.biojavax.bio.seq.RichSequence;

/**
 *
 * @author slotta
 */
public class ProteinInfo implements Serializable {

    private int length;
    private String name;
    private String description;
    private String sequence;
    private double mass;

    /** Creates a new instance of ProteinInfo */
    public ProteinInfo() {
        length = 0;
        description = "";
        sequence = "";
        name = "";
        mass = -1;
    }

    public ProteinInfo(RichSequence rs) {
        name = rs.getName();
        description = rs.getDescription();
        sequence = rs.seqString();
        length = rs.length();
    }

    public ProteinInfo(String s) {
        name = s;
        length = 0;
        description = "";
        sequence = "";
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
        length = sequence.length();
    }

    public RichSequence getRichSequence() {
        if (sequence.length() <= 0) {
            return null;
        }
        try {
            RichSequence rs = RichSequence.Tools.createRichSequence(name, sequence, ProteinTools.getAlphabet());
            rs.setDescription(description);
            return rs;
        } catch (BioException ex) {
            return null;
        }
    }

    public void updateFromRichSequence(RichSequence rs) {
        if (rs.getName().equals(name)) {
            description = rs.getDescription();
            sequence = rs.seqString();
            length = rs.length();
        } else {
            System.err.println("Trying to update " + name + " with data from " + rs.getName());
        }
    }

    public void update(ProteinInfo pInfo) {
        if (pInfo.getName().equals(name)) {
            if (description.length() < pInfo.getDescription().length()) {
                description = pInfo.getDescription();
            }
            if (sequence.length() < pInfo.getSequence().length()) {
                sequence = pInfo.getSequence();
            }
            if (length < pInfo.getLength()) {
                length = pInfo.getLength();
            }
        } else {
            System.err.println("Trying to update " + name + " with data from " + pInfo.getName());
        }
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    @Override
    /**
     * Auto generated
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProteinInfo other = (ProteinInfo) obj;
        if (this.length != other.length) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.sequence == null) ? (other.sequence != null) : !this.sequence.equals(other.sequence)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mass) != Double.doubleToLongBits(other.mass)) {
            return false;
        }
        return true;
    }

    @Override
    /**
     * Auto generated
     */
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.length;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + (this.sequence != null ? this.sequence.hashCode() : 0);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.mass) ^ (Double.doubleToLongBits(this.mass) >>> 32));
        return hash;
    }

    @Override
    /**
     * Auto generated
     */
    public String toString() {
        return "ProteinInfo{" + "name=" + name + "description=" + description + "sequence=" + sequence + '}';
    }
}
