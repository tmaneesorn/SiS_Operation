package sis.com.sis.sis_app.ShipToApproval.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import sis.com.sis.sis_app.ShipToApproval.Models.NotesObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomTextView;


public class NoteListAdapter extends BaseAdapter {

    private Context mContext;
    private List<NotesObject> mList;
    private static LayoutInflater inflater=null;

    private NoteListAdapter.ListViewItemClickListener mListener;


    public NoteListAdapter(Context context, List<NotesObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewItemClickListener(NoteListAdapter.ListViewItemClickListener listener)
    {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder implements View.OnClickListener
    {
        private NoteListAdapter.ListViewItemClickListener mListener;

        CustomTextView tvFileName;
        ImageView ivFileName;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        NoteListAdapter.Holder holder=new NoteListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.shipto_listview_notes_item, null);

        holder.tvFileName=(CustomTextView) rowView.findViewById(R.id.textViewFileName);
        holder.ivFileName=(ImageView) rowView.findViewById(R.id.imageViewFileName);

        NotesObject object = mList.get(position);

        holder.tvFileName.setText(object.file_name);
        holder.ivFileName.setImageResource(R.drawable.ic_document_blue);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position, mList);
            }
        });

        return rowView;
    }

    public interface ListViewItemClickListener {

        void onItemClick(View view, int position, List<NotesObject> mList);

        void listViewDidScrollToEnd();
    }

}
