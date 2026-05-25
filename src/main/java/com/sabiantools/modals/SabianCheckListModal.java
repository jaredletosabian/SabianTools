package com.sabiantools.modals;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;

import com.sabiantools.R;

import java.util.ArrayList;
import java.util.List;

public class SabianCheckListModal extends SabianListModal {

    Integer checkBoxColor;

    private OnCheckListItemsSelectedListener onCheckListItemsSelectedListener;

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
        super.initElements();
    }

    private void onAllSelected() {
        if (onCheckListItemsSelectedListener == null) return;
        final ArrayList<ListCheckItem> selected = new ArrayList<>();
        for (ListItem item : listItems) {
            ListCheckItem cItem = (ListCheckItem) item;
            if (cItem.isChecked()) {
                selected.add(cItem);
            }
        }
        onCheckListItemsSelectedListener.onSelected(selected);
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

    public SabianCheckListModal setOnCheckListItemsSelectedListener(OnCheckListItemsSelectedListener onCheckListItemsSelectedListener) {
        this.onCheckListItemsSelectedListener = onCheckListItemsSelectedListener;
        return this;
    }

    public interface OnCheckListItemsSelectedListener {
        void onSelected(List<ListCheckItem> selected);
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
                final ColorStateList colorList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{
                                color,
                                color
                        }
                );
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

