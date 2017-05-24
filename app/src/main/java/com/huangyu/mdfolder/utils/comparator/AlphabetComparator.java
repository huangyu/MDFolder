package com.huangyu.mdfolder.utils.comparator;

import java.io.File;
import java.text.CollationKey;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by huangyu on 2017-5-24.
 */
public class AlphabetComparator implements Comparator<File> {

    private RuleBasedCollator collator = null;

    public AlphabetComparator() {
        collator = (RuleBasedCollator) Collator.getInstance(Locale.getDefault());
    }

    public int compare(File file1, File file2) {
        CollationKey c1 = collator.getCollationKey(file1.getName());
        CollationKey c2 = collator.getCollationKey(file2.getName());
        return collator.compare((c1).getSourceString(), (c2).getSourceString());
    }

}
