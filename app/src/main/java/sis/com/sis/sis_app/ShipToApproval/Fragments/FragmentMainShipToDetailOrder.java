package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity;
import sis.com.sis.sis_app.ShipToApproval.Adapters.NoteListAdapter;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.ShipToApproval.Models.ApprovedByObject;
import sis.com.sis.sis_app.ShipToApproval.Models.AttachObject;
import sis.com.sis.sis_app.ShipToApproval.Models.CustomerObject;
import sis.com.sis.sis_app.ShipToApproval.Models.DetailObject;
import sis.com.sis.sis_app.ShipToApproval.Models.HeaderDataObject;
import sis.com.sis.sis_app.ShipToApproval.Models.NotesObject;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToDataObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomButton;
import sis.com.sis.sis_app.Views.CustomTextView;

public class FragmentMainShipToDetailOrder extends Fragment implements NoteListAdapter.ListViewItemClickListener {

    @BindView(R.id.customButtonApprove) CustomButton customButtonApprove;
    @BindView(R.id.customButtonResponse)CustomButton customButtonResponse;
    @BindView(R.id.customButtonNotApprove)CustomButton customButtonNotApprove;

    @BindView(R.id.textViewStatus) CustomTextView textViewStatus;

    @BindView(R.id.textViewOrderNo) CustomTextView textViewOrderNo;
    @BindView(R.id.textViewSaleOrg) CustomTextView textViewSaleOrg;
    @BindView(R.id.textViewOrderType) CustomTextView textViewOrderType;
    @BindView(R.id.textViewCustomer) CustomTextView textViewCustomer;
    @BindView(R.id.textViewShipTo) CustomTextView textViewShipTo;
    @BindView(R.id.textViewPriceBlock) CustomTextView textViewPriceBlock;
    @BindView(R.id.textViewAmount) CustomTextView textViewAmount;
    @BindView(R.id.textViewReason) CustomTextView textViewReason;

//    @BindView(R.id.textViewOrderDate) CustomTextView textViewOrderDate;
//    @BindView(R.id.textViewPoNo) CustomTextView textViewPoNo;
//    @BindView(R.id.textViewPoDate) CustomTextView textViewPoDate;
//    @BindView(R.id.textViewSale) CustomTextView textViewSale;
//    @BindView(R.id.textViewCredit) CustomTextView textViewCredit;
//    @BindView(R.id.textViewTeam) CustomTextView textViewTeam;
//    @BindView(R.id.textViewPaymentTerm) CustomTextView textViewPaymentTerm;
//
//    @BindView(R.id.textViewCustomerCompany) CustomTextView textViewCustomerCompany;
//    @BindView(R.id.textViewCustomerContact) CustomTextView textViewCustomerContact;
//    @BindView(R.id.textViewCustomerTel) CustomTextView textViewCustomerTel;
//    @BindView(R.id.textViewCustomerBuildNo) CustomTextView textViewCustomerBuildNo;
//    @BindView(R.id.textViewCustomerRoad) CustomTextView textViewCustomerRoad;
    @BindView(R.id.textViewCustomerDistrict) CustomTextView textViewCustomerDistrict;
    @BindView(R.id.textViewCustomerProvince) CustomTextView textViewCustomerProvince;
//    @BindView(R.id.textViewCustomerPostCode) CustomTextView textViewCustomerPostCode;

    @BindView(R.id.textViewShipToStatus) CustomTextView textViewShipToStatus;
    @BindView(R.id.textViewShipToCompany) CustomTextView textViewShipToCompany;
    @BindView(R.id.textViewShipToContact) CustomTextView textViewShipToContact;
    @BindView(R.id.textViewShipToTel) CustomTextView textViewShipToTel;
    @BindView(R.id.textViewShipToBuildNo) CustomTextView textViewShipToBuildNo;
    @BindView(R.id.textViewShipToRoad) CustomTextView textViewShipToRoad;
    @BindView(R.id.textViewShipToDistrict) CustomTextView textViewShipToDistrict;
    @BindView(R.id.textViewShipToProvince) CustomTextView textViewShipToProvince;
//    @BindView(R.id.textViewShipToPostCode) CustomTextView textViewShipToPostCode;

    @BindView(R.id.textViewApprovedSM) CustomTextView textViewApprovedSM;
    @BindView(R.id.textViewApprovedSMStatus) CustomTextView textViewApprovedSMStatus;
    @BindView(R.id.textViewReasonSM) CustomTextView textViewReasonSM;
    @BindView(R.id.relativeLayoutReasonSM) RelativeLayout relativeLayoutReasonSM;
    @BindView(R.id.textViewApprovedGM) CustomTextView textViewApprovedGM;
    @BindView(R.id.textViewApprovedGMStatus) CustomTextView textViewApprovedGMStatus;
    @BindView(R.id.textViewReasonGM) CustomTextView textViewReasonGM;
    @BindView(R.id.relativeLayoutReasonGM) RelativeLayout relativeLayoutReasonGM;

    @BindView(R.id.textViewNoPO) CustomTextView textViewNoPO;
    @BindView(R.id.textViewNoIDCard) CustomTextView textViewNoIDCard;
    @BindView(R.id.textViewNoMap) CustomTextView textViewNoMap;
    @BindView(R.id.textViewExpense) CustomTextView textViewExpense;
    @BindView(R.id.textViewCase) CustomTextView textViewCase;
    @BindView(R.id.linearLayoutExpense) LinearLayout linearLayoutExpense;
    @BindView(R.id.linearLayoutCase) LinearLayout linearLayoutCase;

    @BindView(R.id.textViewNotes) CustomTextView textViewNotes;

    DetailObject orderDetail;
    HeaderDataObject headerData;
    CustomerObject customerData;
    ShipToDataObject shipToData;
    ApprovedByObject approvedData;
    AttachObject attachData;
    ArrayList<NotesObject> notesObject;
    ArrayList<NotesObject> poObject;
    ArrayList<NotesObject> idCardObject;
    ArrayList<NotesObject> mapObject;

    String so_no;
    String username;
    String notesData;
    String docOneTime;

    String order_type = "";
    String price_block = "";

    View view;

    @BindView(R.id.recycleViewNotes) GridView mListView;
    NoteListAdapter noteListAdapter;
    List<NotesObject> arrayList;

    @BindView(R.id.recycleViewPO) GridView mListViewPO;
    NoteListAdapter noteListAdapterPO;
    List<NotesObject> arrayListPO;

    @BindView(R.id.recycleViewIDCard) GridView mListViewIDCard;
    NoteListAdapter noteListAdapterIDCard;
    List<NotesObject> arrayListIDCard;

    @BindView(R.id.recycleViewMap) GridView mListViewMap;
    NoteListAdapter noteListAdapterMap;
    List<NotesObject> arrayListMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.shipto_fragment_main_shipto_detail_order, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_sale_order_detail);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        orderDetail = (DetailObject) bundle.getSerializable("orderDetail");
        headerData = (HeaderDataObject) bundle.getSerializable("headerData");
        customerData = (CustomerObject) bundle.getSerializable("customerData");
        shipToData = (ShipToDataObject) bundle.getSerializable("shipToData");
        approvedData = (ApprovedByObject) bundle.getSerializable("approvedData");
        attachData = (AttachObject) bundle.getSerializable("attachData");
        notesObject = (ArrayList<NotesObject>) bundle.getSerializable("notesAttach");
        poObject = (ArrayList<NotesObject>) bundle.getSerializable("POAttach");
        idCardObject = (ArrayList<NotesObject>) bundle.getSerializable("IDCardAttach");
        mapObject = (ArrayList<NotesObject>) bundle.getSerializable("MapAttach");
        notesData = String.valueOf(bundle.getSerializable("notesData"));
        docOneTime = String.valueOf(bundle.getSerializable("docOneTime"));
        username = String.valueOf(bundle.getSerializable("username"));
        so_no = String.valueOf(bundle.getSerializable("so_no"));

        arrayList = new ArrayList<NotesObject>();
        noteListAdapter = new NoteListAdapter(getContext(), arrayList);
        noteListAdapter.setListViewItemClickListener(this);
        mListView.setAdapter(noteListAdapter);
        loadNoteFiles();

        arrayListPO = new ArrayList<NotesObject>();
        noteListAdapterPO = new NoteListAdapter(getContext(), arrayListPO);
        noteListAdapterPO.setListViewItemClickListener(this);
        mListViewPO.setAdapter(noteListAdapterPO);
        loadPOFiles();

        arrayListIDCard = new ArrayList<NotesObject>();
        noteListAdapterIDCard = new NoteListAdapter(getContext(), arrayListIDCard);
        noteListAdapterIDCard.setListViewItemClickListener(this);
        mListViewIDCard.setAdapter(noteListAdapterIDCard);
        loadIDCardFiles();

        arrayListMap = new ArrayList<NotesObject>();
        noteListAdapterMap = new NoteListAdapter(getContext(), arrayListMap);
        noteListAdapterMap.setListViewItemClickListener(this);
        mListViewMap.setAdapter(noteListAdapterMap);
        loadMapFiles();

        textViewOrderNo.setText(orderDetail.order_detail_no);
        if (orderDetail.order_detail_org.equals("1000")){
            textViewSaleOrg.setText("SiS Distribution");
        }
        else if (orderDetail.order_detail_org.equals("2000")){
            textViewSaleOrg.setText("Qool Distribution");
        }
        else if (orderDetail.order_detail_org.equals("4000")){
            textViewSaleOrg.setText("PGS");
        }
        else if (orderDetail.order_detail_org.equals("8000")){
            textViewSaleOrg.setText("Wiko Mobile");
        }

        String amount;
        if (!orderDetail.order_detail_total.equals("0")){
            amount = new DecimalFormat("#,###.00").format(Double.parseDouble(orderDetail.order_detail_total));
        }
        else {
            amount = "0.00";
        }

        textViewOrderType.setText(orderDetail.order_detail_type + order_type);
        textViewCustomer.setText(customerData.cus_addr_name);
        textViewShipTo.setText(shipToData.shipto_addr_name);
        textViewPriceBlock.setText(orderDetail.order_detail_priceblock + price_block);
        textViewAmount.setText(amount);
        if (orderDetail.order_detail_reasonshipto.equals("-") || orderDetail.order_detail_reasonshipto.equals("")) {
            textViewReason.setText(R.string.message_no_reason);
        }
        else {
            textViewReason.setText(orderDetail.order_detail_reasonshipto);
        }

//        textViewOrderDate.setText(headerData.header_data_date);
//        textViewPoNo.setText(headerData.header_data_pono);
//        textViewPoDate.setText(headerData.header_data_podate);
//        textViewSale.setText(headerData.header_data_sale);
//        textViewCredit.setText(headerData.header_data_credit);
//        textViewTeam.setText(headerData.header_data_team);
//        textViewPaymentTerm.setText(headerData.header_data_payterm);
//
//        textViewCustomerCompany.setText(customerData.cus_addr_name);
//        textViewCustomerContact.setText(customerData.cus_addr_contactperson);
//        textViewCustomerTel.setText(customerData.cus_addr_phone);
//        textViewCustomerBuildNo.setText(customerData.cus_addr_buildno);
//        textViewCustomerRoad.setText(customerData.cus_addr_road);
        textViewCustomerDistrict.setText(customerData.cus_addr_district);
        textViewCustomerProvince.setText(customerData.cus_addr_province);
//        textViewCustomerPostCode.setText(customerData.cus_addr_postcode);

        if (docOneTime.equals("0")){
            textViewShipToStatus.setText(R.string.main_ship_to_permanent);
        }
        else {
            textViewShipToStatus.setText(R.string.main_ship_to_onetime);
        }

        textViewShipToCompany.setText(shipToData.shipto_addr_name);
        textViewShipToContact.setText(shipToData.shipto_addr_contactperson);
        textViewShipToTel.setText(shipToData.shipto_addr_phone);
        textViewShipToBuildNo.setText(shipToData.shipto_addr_buildno);
        textViewShipToRoad.setText(shipToData.shipto_addr_road);
        textViewShipToDistrict.setText(shipToData.shipto_addr_district);
        textViewShipToProvince.setText(shipToData.shipto_addr_province);
//        textViewShipToPostCode.setText(shipToData.shipto_addr_postcode);


        textViewApprovedGM.setText(approvedData.approve_log_gm_by);
        if (approvedData.approve_log_gm.equals("-")){
            textViewApprovedGMStatus.setText(R.string.main_document_status_waiting);
            textViewApprovedGMStatus.setTextColor(getResources().getColor(R.color.colorYellow));
            relativeLayoutReasonGM.setVisibility(View.GONE);
        }
        else if (approvedData.approve_log_gm.equals("Approve")){
            textViewApprovedGMStatus.setText(getString(R.string.main_document_status_approve) + " " + approvedData.approve_log_gm_timestamp);
            textViewApprovedGMStatus.setTextColor(getResources().getColor(R.color.colorGreen));
            textViewReasonGM.setText(approvedData.approve_log_gm_reason);
        }

        textViewApprovedSM.setText(approvedData.approve_log_sm_by);
        if (approvedData.approve_log_sm.equals("-")){
            textViewApprovedSMStatus.setText(R.string.main_document_status_waiting);
            textViewApprovedSMStatus.setTextColor(getResources().getColor(R.color.colorYellow));
            relativeLayoutReasonSM.setVisibility(View.GONE);
            relativeLayoutReasonGM.setVisibility(View.GONE);
        }
        else if (approvedData.approve_log_sm.equals("Approve")){
            textViewApprovedSMStatus.setText(getString(R.string.main_document_status_approve) + " " + approvedData.approve_log_sm_timestamp);
            textViewApprovedSMStatus.setTextColor(getResources().getColor(R.color.colorGreen));
            textViewReasonSM.setText(approvedData.approve_log_sm_reason);
        }
        else {
            textViewApprovedSMStatus.setText(getString(R.string.main_document_status_not_approve) + " " + approvedData.approve_log_sm_timestamp);
            textViewApprovedSMStatus.setTextColor(getResources().getColor(R.color.colorRed));
            textViewReasonSM.setText(approvedData.approve_log_sm_reason);

            textViewApprovedGMStatus.setTextColor(getResources().getColor(R.color.colorRed));
            textViewApprovedGMStatus.setText("ไม่อนุมัติเนื่องจาก SM ไม่อนุมัติ");

            textViewStatus.setText(R.string.main_document_status_not_approve);
            textViewStatus.setTextColor(getResources().getColor(R.color.colorRed));
        }

        if (approvedData.approve_log_sm.equals("-") && approvedData.approve_log_gm.equals("-")){
            textViewStatus.setText(R.string.main_document_status_waiting);
        }
        else if (approvedData.approve_log_gm.equals("-")){
            textViewStatus.setText(R.string.main_document_status_waiting);
        }

        if (notesData.equals("1")){
            textViewNotes.setText(R.string.message_cannot_display_content);
        }
        else {
            textViewNotes.setText(R.string.message_no_content);
        }

        if (arrayListPO.size()==0){
            textViewNotes.setText(R.string.message_no_attach);
            mListViewPO.setVisibility(View.GONE);
            textViewNoPO.setVisibility(View.VISIBLE);
        }
        if (arrayListIDCard.size()==0){
            textViewNotes.setText(R.string.message_no_attach);
            mListViewIDCard.setVisibility(View.GONE);
            textViewNoIDCard.setVisibility(View.VISIBLE);
        }
        if (arrayListMap.size()==0){
            textViewNotes.setText(R.string.message_no_attach);
            mListViewMap.setVisibility(View.GONE);
            textViewNoMap.setVisibility(View.VISIBLE);
        }


        if (attachData.shipto_customercase.equals("-")){
            linearLayoutCase.setVisibility(View.GONE);
        }
        else {
            textViewExpense.setText(attachData.shipto_customercase);
        }

        if (attachData.shipto_expense.equals("0")){
            linearLayoutExpense.setVisibility(View.GONE);
        }
        else {
            textViewExpense.setText(attachData.shipto_expense);
        }

        if (notesObject.size() == 0) {
            mListView.setVisibility(View.GONE);
        }

        return view;
    }

    private void loadNoteFiles() {

        for (NotesObject item: notesObject)
        {
            Constants.doLog("ITEMS NOTES : " + item);
            arrayList.add(item);
        }
    }

    private void loadPOFiles() {

        for (NotesObject item: poObject)
        {
            Constants.doLog("ITEMS NOTES : " + item);
            arrayListPO.add(item);
        }
    }

    private void loadIDCardFiles() {

        for (NotesObject item: idCardObject)
        {
            Constants.doLog("ITEMS NOTES : " + item);
            arrayListIDCard.add(item);
        }
    }

    private void loadMapFiles() {

        for (NotesObject item: mapObject)
        {
            Constants.doLog("ITEMS NOTES : " + item);
            arrayListMap.add(item);
        }
    }


    @OnClick(R.id.customButtonApprove)
    public void btnApprovePressed(CustomButton button)
    {
        FragmentMainShipToResponse fragmentMainShipToResponse = new FragmentMainShipToResponse();

        Bundle bundle = new Bundle();
        bundle.putSerializable("so_no",so_no);
        bundle.putSerializable("username",username);
        bundle.putSerializable("action","app");
        fragmentMainShipToResponse.setArguments(bundle);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_button_approve);
        ((MainActivity)getActivity()).replaceFragment(fragmentMainShipToResponse,true);
    }

    @OnClick(R.id.customButtonResponse)
    public void btnResponsePressed(CustomButton button)
    {
        FragmentMainShipToResponse fragmentMainShipToResponse = new FragmentMainShipToResponse();

        Bundle bundle = new Bundle();
        bundle.putSerializable("so_no",so_no);
        bundle.putSerializable("username",username);
        bundle.putSerializable("action","response");
        fragmentMainShipToResponse.setArguments(bundle);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_button_response);
        ((MainActivity)getActivity()).replaceFragment(fragmentMainShipToResponse,true);
    }

    @OnClick(R.id.customButtonNotApprove)
    public void btnNotApprovePressed(CustomButton button)
    {
        FragmentMainShipToResponse fragmentMainShipToResponse = new FragmentMainShipToResponse();

        Bundle bundle = new Bundle();
        bundle.putSerializable("so_no",so_no);
        bundle.putSerializable("username",username);
        bundle.putSerializable("action","reject");
        fragmentMainShipToResponse.setArguments(bundle);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_button_not_approve);
        ((MainActivity)getActivity()).replaceFragment(fragmentMainShipToResponse,true);
    }

    @Override
    public void onItemClick(View view, int position, List<NotesObject> mList) {
        Bundle bundle = new Bundle();
        NotesObject notes = null;
        if (mList.toString().equals(arrayListPO.toString())){
            bundle.putSerializable("type_doc", "po");
            notes = arrayListPO.get(position);
        }
        else if (mList.toString().equals(arrayListIDCard.toString())){
            bundle.putSerializable("type_doc", "id_card");
            notes = arrayListIDCard.get(position);
        }
        else if (mList.toString().equals(arrayListMap.toString())){
            bundle.putSerializable("type_doc", "map");
            notes = arrayListMap.get(position);
        }
        else if (mList.toString().equals(arrayList.toString())){
            bundle.putSerializable("type_doc", "notes");
            notes = arrayList.get(position);
        }

        Constants.doLog("Item was clicked" + notes.file_name);
        Constants.doLog("Item was clicked URL " + notes.path_full);

        FragmentMainDocumentReview fragmentMainDocumentReview = new FragmentMainDocumentReview();

        bundle.putSerializable("url", notes.path_full);
        fragmentMainDocumentReview.setArguments(bundle);
        ((MainActivity)getActivity()).replaceFragment(fragmentMainDocumentReview,true);
//        PopupBrowser popupMenu = new PopupBrowser(getActivity());
        Constants.doLog("Loading " + notes.path_full);
//        if (view != null) popupMenu.showWithUrl(view, notes.path_full);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void listViewDidScrollToEnd() {

    }
}
