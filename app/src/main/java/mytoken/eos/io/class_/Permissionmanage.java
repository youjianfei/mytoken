package mytoken.eos.io.class_;

import mytoken.eos.io.Interface.InterfacePermission;
import com.master.permissionhelper.PermissionHelper;

public class Permissionmanage {
    private PermissionHelper permissionHelper;
    private InterfacePermission interfacePermission;


    public Permissionmanage(PermissionHelper permissionHelper, InterfacePermission interfacePermission) {
        this.permissionHelper = permissionHelper;
        this.interfacePermission=interfacePermission;

    }


    public  void requestpermission(){
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                //全都授权
                interfacePermission.onResult(true);
            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
                //某个授权
                interfacePermission.onResult(true);

            }

            @Override
            public void onPermissionDenied() {
                //某个拒绝
                interfacePermission.onResult(false);

            }

            @Override
            public void onPermissionDeniedBySystem() {
                //用户选择了"不再询问"后，点击"拒绝按钮"，执行此方法
                interfacePermission.onResult(false);

            }
        });
    }
}
