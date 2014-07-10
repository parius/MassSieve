package gov.nih.nimh.mass_sieve.gui;

/**
 * TODO Describe class
 * <p/>
 * <p/>
 * Created at: Oct 27, 2010 11:38:55 AM
 *
 * @author Alexey Tigarev
 */
public class FieldColumn {
    private String title;
    private String fieldName;

    public FieldColumn(String fieldName, String title) {
        this.fieldName = fieldName;
        this.title = title;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MyColumn");
        sb.append("{fieldName='").append(fieldName).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
