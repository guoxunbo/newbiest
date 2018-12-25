import EntityForm from "./EntityForm";
import UserManagerRequest from "../../api/user-manager/UserManagerRequest";

export default class UserForm extends EntityForm {

    handleSave = (values) => {
        let self = this;
        let object = {
            user: values,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.user);
                }
            }
        }
        UserManagerRequest.sendMergeUserRequest(object);
    }
}