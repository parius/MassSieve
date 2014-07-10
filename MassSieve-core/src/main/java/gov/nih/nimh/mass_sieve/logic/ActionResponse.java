package gov.nih.nimh.mass_sieve.logic;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ActionResponse {

    public static ActionResponse SUCCESS = new ActionResponse(ActionResult.SUCCESS);
    public final long actionId;
    public final ActionResult actionResult;
    public final String message;

    private ActionResponse(ActionResult result) {
        actionId = -1;
        message = "";
        actionResult = result;
    }

    public ActionResponse(ActionResult result, String msg) {
        actionId = -1;
        message = msg;
        actionResult = result;
    }

    public boolean isFailed() {
        return actionResult != ActionResult.SUCCESS;
    }
}
