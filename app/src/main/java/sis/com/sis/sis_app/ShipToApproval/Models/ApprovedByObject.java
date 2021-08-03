package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class ApprovedByObject implements Serializable {

    public String approve_log_sm;
    public String approve_log_sm_by;
    public String approve_log_gm_timestamp;
    public String approve_log_sm_reason;
    public String approve_log_gm;
    public String approve_log_gm_by;
    public String approve_log_sm_timestamp;
    public String approve_log_gm_reason;
    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s)", approve_log_sm, approve_log_sm_by, approve_log_gm_timestamp, approve_log_sm_reason, approve_log_gm, approve_log_gm_by, approve_log_sm_timestamp, approve_log_gm_reason);
    }
}
