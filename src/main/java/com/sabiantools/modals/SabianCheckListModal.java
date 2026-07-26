package com.sabiantools.modals;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;

import com.sabiantools.R;
import com.sabiantools.utilities.SabianToast;
import com.sabiantools.utilities.SabianUtilities;

import java.util.ArrayList;
import java.util.List;

public class SabianCheckListModal extends SabianListModal {

    private Integer checkBoxColor;

    private boolean allowAddNew;

    private String addNewText;

    private View btnAddNew;

    private TextView txtAddNew;

    public void setAllowAddNew(boolean allowAddNew) {
        this.allowAddNew = allowAddNew;
    }

    public void setAddNewText(String addNewText) {
        this.addNewText = addNewText;
    }

    private OnCheckListItemsListener onCheckListItemsListener;

    public SabianCheckListModal(@NonNull Context context) {
        super(context);
    }

    public SabianCheckListModal(@NonNull Context context, String... items) {
        super(context, items);
    }

    public SabianCheckListModal(@NonNull Context context, int... items) {
        super(context, items);
    }

    public SabianCheckListModal(@NonNull Context context, Object... items) {
        super(context, items);
    }

    public void setCheckBoxColor(Integer checkBoxColor) {
        this.checkBoxColor = checkBoxColor;
    }

    @Override
    protected void initElements() {
        setOnOkayClickListener(view -> onAllSelected());
        setOnCancelClickListener(view -> dismiss());
        initAddNewOptionElements();
        super.initElements();
    }

    protected void initAddNewOptionElements() {
        txtAddNew = findViewById(R.id.sct_SabianModalZeroAddNew);
        if (!SabianUtilities.IsStringBlankOrEmpty(addNewText)) {
            txtAddNew.setText(addNewText);
        }
        btnAddNew = findViewById(R.id.rll_SabianModalZeroAddNew);
        btnAddNew.setOnClickListener(view -> {
            addNew(getSearchText());
        });
    }

    private void addNew(@Nullable String value) {
        if (value == null || SabianUtilities.IsStringBlankOrEmpty(value)) {
            Context context = getContext();
            SabianUtilities.DisplayMessage(context, context.getString(R.string.please_enter_text_in_the_search_bar));
            return;
        }
        ListCheckItem item = onCheckListItemsListener.onAddNew(value);
        if (item == null) {
            item = new ListCheckItem(value);
            item.setID(SabianUtilities.GetCurrentTimestamp());
        }
        if (listItems == null) listItems = new ArrayList<>();
        final int index = listItems.indexOf(item);
        if (index > -1) {
            item = (ListCheckItem) listItems.get(index);
            item.setChecked(true);
            listItems.remove(index);
            listItems.add(0, item);
        } else {
            item.setChecked(true);
            listItems.add(0, item);
        }
        if (adapter == null) return;
        adapter.notifyDataSetChanged();
    }

    private void onAllSelected() {
        if (onCheckListItemsListener == null) return;
        final ArrayList<ListCheckItem> selected = new ArrayList<>();
        for (ListItem item : listItems) {
            ListCheckItem cItem = (ListCheckItem) item;
            if (cItem.isChecked()) {
                selected.add(cItem);
            }
        }
        onCheckListItemsListener.onSelected(selected);
        dismiss();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.sabian_modal_check_list;
    }

    @Override
    protected ListItemAdapter createListAdapter(List<ListItem> listItems) {
        final CheckListAdapter adapter = new CheckListAdapter(getContext(), -1, listItems);
        adapter.setCheckBoxColor(checkBoxColor);
        return adapter;
    }

    @Override
    protected void onListItemSelected(ListItem item) {
        ListCheckItem cItem = (ListCheckItem) item;
        cItem.setChecked(!cItem.isChecked());
        updateItem(cItem, true);
    }

    public SabianCheckListModal setOnCheckListItemsSelectedListener(OnCheckListItemsListener onCheckListItemsListener) {
        this.onCheckListItemsListener = onCheckListItemsListener;
        return this;
    }

    public interface OnCheckListItemsListener {
        void onSelected(List<ListCheckItem> selected);

        @Nullable
        default ListCheckItem onAddNew(@NonNull String newItem) {
            return null;
        }
    }

    private static class CheckListAdapter extends SabianListModal.ListItemAdapter {

        Integer checkBoxColor;

        public void setCheckBoxColor(Integer checkBoxColor) {
            this.checkBoxColor = checkBoxColor;
        }

        public CheckListAdapter(@NonNull Context context, int resource, @NonNull List<ListItem> objects) {
            super(context, resource, objects);
        }

        @Override
        protected int getLayoutRes() {
            return R.layout.sabian_modal_check_list_item;
        }

        @Override
        protected void bindHolderItem(Holder holder, ListItem item) {
            super.bindHolderItem(holder, item);
            CheckListHolder cHolder = (CheckListHolder) holder;
            ListCheckItem cItem = (ListCheckItem) item;
            cHolder.checkBox.setChecked(cItem.isChecked());
        }

        @Override
        protected Holder createHolder() {
            return new CheckListHolder();
        }

        @Override
        protected void initHolder(View view, Holder holder) {
            super.initHolder(view, holder);
            CheckListHolder cHolder = (CheckListHolder) holder;
            cHolder.checkBox = view.findViewById(R.id.cbx_ViewHoldermodalCheckBox);
            if (checkBoxColor != null) {
                final int color = checkBoxColor;
                final ColorStateList colorList = new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}}, new int[]{color, color});
                CompoundButtonCompat.setButtonTintList(cHolder.checkBox, colorList);
            }
        }
    }

    public static class ListCheckItem extends SabianListModal.ListItem {

        private boolean isChecked = false;

        public boolean isChecked() {
            return isChecked;
        }

        public ListCheckItem setChecked(boolean checked) {
            isChecked = checked;
            return this;
        }

        public ListCheckItem(String title) {
            super(title);
        }

        public ListCheckItem(long ID, String title) {
            super(ID, title);
        }

        public ListCheckItem(String title, String subTitle) {
            super(title, subTitle);
        }

        public ListCheckItem(String title, String subTitle, int imageRes) {
            super(title, subTitle, imageRes);
        }

        @Override
        public ListCheckItem setValue(Object value) {
            super.setValue(value);
            return this;
        }
    }

    private static class CheckListHolder extends SabianListModal.Holder {
        AppCompatCheckBox checkBox;
    }
}

