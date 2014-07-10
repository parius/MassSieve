package gov.nih.nimh.mass_sieve.gui;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TableFormat builder.
 * <p>Allows to build <code>TableFormat</code>s easier: by adding columns with field name and title at once,
 * instead of separately building an array of field names and an array of files.
 * 
 * @see gov.nih.nimh.mass_sieve.gui.PeptideHitListPanel - an example of usage
 * @author Alexey Tigarev
 */
class TableFormatBuilder {
    private List<FieldColumn> columns = new ArrayList<FieldColumn>();

    public TableFormat createTableFormat() {
        String [] columnTitles = new String[columns.size()];
        String [] columnFields = new String[columns.size()];
        for (int i=0; i < columns.size(); i++) {
            columnTitles[i] = columns.get(i).getTitle();
            columnFields[i] = columns.get(i).getFieldName();
        }
        return GlazedLists.tableFormat(columnFields, columnTitles);
    }

    private void addColumn(String title, String fieldName) {
        addColumn(new FieldColumn(fieldName, title));
    }

    private void addColumn(FieldColumn myColumn) {
        columns.add(myColumn);
    }

    public void addColumns(FieldColumn... moreColumns) {
        columns.addAll(Arrays.asList(moreColumns));
    }
}
