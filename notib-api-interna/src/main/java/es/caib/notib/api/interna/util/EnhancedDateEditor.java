package es.caib.notib.api.interna.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EnhancedDateEditor extends PropertyEditorSupport {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @Nullable
    private final DateFormat dateFormat;
    private final boolean allowEmpty;

    public EnhancedDateEditor(boolean allowEmpty) {
        this((DateFormat)null, allowEmpty);
    }

    public EnhancedDateEditor(@Nullable DateFormat dateFormat, boolean allowEmpty) {
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
    }

    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        String input = text != null ? text.trim() : null;
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            this.setValue((Object)null);
        } else {
            this.setValue(parseDateWithFallback(text));
        }

    }

    public Date parseDateWithFallback(String text) throws IllegalArgumentException {
        if (this.dateFormat != null) {
            return parseWithFallback(dateFormat, text);
        } else {
            return parseWithFallback(DATE_FORMAT, text);
        }
    }

    private Date parseWithFallback(DateFormat primaryFormat, String text) throws IllegalArgumentException {
        try {
            return primaryFormat.parse(text);
        } catch (ParseException pex) {
            try {
                return DATE_FORMAT.parse(text);
            } catch(ParseException pex2) {
                throw new IllegalArgumentException("Could not parse date: " + pex2.getMessage(), pex2);
            }
        }
    }

    public String getAsText() {
        DateFormat df = this.dateFormat != null ? this.dateFormat : DATE_FORMAT;
        Date value = (Date)this.getValue();
        return value != null ? df.format(value) : "";
    }
}
