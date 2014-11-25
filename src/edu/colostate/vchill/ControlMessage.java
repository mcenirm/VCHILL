package edu.colostate.vchill;

/**
 * Command object used by Controller to queue up requests for
 * the data acquisition backend.  Since ControlMessage objects
 * are used as keys in the cache, each ControlMessage must be
 * immutable.
 *
 * @author Jochen Deyke
 * @author jpont
 * @version 2008-06-26
 */
public final class ControlMessage {
    private final String url;
    private final String dir;
    private final String file;
    private final String sweep;

    /**
     * String used to separate fields in the toString() method.
     * Can be used with String.split to re-create the separate fields.
     */
    public static final String separator = "*";

    /**
     * Constructs a ControlMessage from a toString()ed one
     *
     * @param whole a string as generated by toString()
     */
    public ControlMessage(final String whole) {
        String[] parts = whole.split("\\" + ControlMessage.separator); //since separator is 'special', we need to escape it
        assert parts.length < 6;
        assert parts.length > 3;
        this.url = parts[0].replace("%20", " ");
        this.dir = parts[1].replace("%20", " ");
        this.file = parts[2].replace("%20", " ");
        this.sweep = parts[3].replace("%20", " ");
    }

    /**
     * Constructs a ControlMessage with no data types
     */
    public ControlMessage(final String url, final String dir,
                          final String file, final String sweep) {
        this.url = url;
        this.dir = dir;
        this.file = file;
        this.sweep = sweep;
    }

    public boolean isValid() {
        if (this.url == null || this.url.trim().equals(""))
            return false;
        if (this.dir == null || this.dir.trim().equals(""))
            return false;
        if (this.file == null || this.file.trim().equals(""))
            return false;
        return !(this.sweep == null || this.sweep.trim().equals(""));

    }

    public String getURL() {
        return this.url;
    }

    public String getDir() {
        return this.dir;
    }

    public String getFile() {
        return this.file;
    }

    public String getSweep() {
        return this.sweep;
    }

    /**
     * Copies this ControlMessage, replacing the field specified
     */
    public ControlMessage setURL(final String url) {
        return new ControlMessage(url, this.dir, this.file, this.sweep);
    }

    public ControlMessage setDir(final String dir) {
        return new ControlMessage(this.url, dir, this.file, this.sweep);
    }

    public ControlMessage setFile(final String file) {
        return new ControlMessage(this.url, this.dir, file, this.sweep);
    }

    public ControlMessage setSweep(final String sweep) {
        return new ControlMessage(this.url, this.dir, this.file, sweep);
    }

    public String toString() {
        return this.url +
                ControlMessage.separator + this.dir +
                ControlMessage.separator + this.file +
                ControlMessage.separator + this.sweep;
    }

    public int hashCode() {
        int hash = 0;
        if (this.url != null) hash ^= this.url.hashCode();
        if (this.dir != null) hash ^= this.dir.hashCode();
        if (this.file != null) hash ^= this.file.hashCode();
        if (this.sweep != null) hash ^= this.sweep.hashCode();
        return hash;
    }

    /**
     * Compares this ControlMessage with another for equality.
     * The comparison is done using String.equals on each field.
     *
     * @param obj The object to camper this object with
     */
    public boolean equals(final Object obj) {
        ControlMessage other;

        try {
            other = (ControlMessage) obj;
        } catch (Exception e) {
            System.err.println("Cannot compare to non-ControlMessage objects");
            return false;
        }

        if (this.url == null) {
            if (other.getURL() != null) return false;
        } else {
            if (!this.url.equals(other.getURL())) return false;
        }

        if (this.dir == null) {
            if (other.getDir() != null) return false;
        } else {
            if (!this.dir.equals(other.getDir())) return false;
        }

        if (this.file == null) {
            if (other.getFile() != null) return false;
        } else {
            if (!this.file.equals(other.getFile())) return false;
        }

        if (this.sweep == null) {
            if (other.getSweep() != null) return false;
        } else {
            if (!this.sweep.equals(other.getSweep())) return false;
        }

        return true;
    }
}
