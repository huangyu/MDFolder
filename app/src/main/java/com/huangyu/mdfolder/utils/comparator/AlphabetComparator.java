package com.huangyu.mdfolder.utils.comparator;

import com.huangyu.mdfolder.bean.FileItem;

import java.util.Comparator;

/**
 * Created by huangyu on 2017-5-24.
 */
public class AlphabetComparator implements Comparator<FileItem> {

//    private RuleBasedCollator collator = null;

    public AlphabetComparator() {
//        collator = (RuleBasedCollator) Collator.getInstance(Locale.getDefault());
    }

    public int compare(FileItem file1, FileItem file2) {
//        CollationKey c1 = collator.getCollationKey(file1.getName());
//        CollationKey c2 = collator.getCollationKey(file2.getName());
//        return collator.compare((c1).getSourceString(), (c2).getSourceString());
        return file1.getName().compareToIgnoreCase(file2.getName());
    }

}
