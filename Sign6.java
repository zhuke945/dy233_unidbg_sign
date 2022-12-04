package com.ss.android.ugc.aweme;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.arm.backend.*;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.Module;
import com.github.unidbg.pointer.UnidbgPointer;

import java.io.File;

public class Sign6 extends AbstractJni {

    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private final Memory memory;

    private Sign6() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .setProcessName("com.ss.android.ugc.aweme")
                .addBackendFactory(new Unicorn2Factory(true))
                .build();
        emulator.getBackend().registerEmuCountHook(100000);
        emulator.getSyscallHandler().setVerbose(true);
        emulator.getSyscallHandler().setEnableThreadDispatcher(true);

        memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction(true);

        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/dy233/dy233.apk"));
        vm.setJni(this);
        vm.setVerbose(true);

        DvmClass a = vm.resolveClass("ms/bd/c/k");
        DvmClass b = vm.resolveClass("ms/bd/c/a0", a);
        DvmClass c = vm.resolveClass("com/bytedance/mobsec/metasec/ml/MS", b);

        DalvikModule dm = vm.loadLibrary("metasec_ml", true);
        module = dm.getModule();
        dm.callJNI_OnLoad(emulator);
        System.out.println("ok");
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        System.out.println("callObjectMethodV "+ signature);
        switch (signature) {
            case "java/lang/Thread->getStackTrace()[Ljava/lang/StackTraceElement;": {
                DvmObject<?>[] a = {
                        vm.resolveClass("java/lang/StackTraceElement").newObject("dalvik.system.VMStack"),
                        vm.resolveClass("java/lang/StackTraceElement").newObject("java.lang.Thread")
                };
                return new ArrayObject(a);
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        System.out.println("callStaticObjectMethodV "+ signature);
        switch (signature) {
            case "com/bytedance/mobsec/metasec/ml/MS->b(IIJLjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;": {
                int a = vaList.getIntArg(0);
                System.out.println("----------------------------");
                System.out.println(a);
                System.out.println("----------------------------");
                if (a == 65539) {
                    return new StringObject(vm,"/data/user/0/com.ss.android.ugc.aweme/files/;o@Y0f");
                } else if (a == 33554433) {
                    return DvmBoolean.valueOf(vm, Boolean.TRUE);
                } else if (a == 33554434) {
                    return DvmBoolean.valueOf(vm, Boolean.TRUE);
                } else if (a == 16777233) {
                    return new StringObject(vm, "23.3.0");
                }
            }
            case "java/lang/Thread->currentThread()Ljava/lang/Thread;": {
                return vm.resolveClass("java/lang/Thread").newObject(Thread.currentThread());
            }
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public void callStaticVoidMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        System.out.println("callStaticVoidMethodV "+ signature);
        switch (signature) {
            case "com/bytedance/mobsec/metasec/ml/MS->a()V": {
                return;
            }
        }
        super.callStaticVoidMethodV(vm, dvmClass, signature, vaList);
    }

    private String GetSign(String url, String header) {
        Number number = module.callFunction(emulator,
                0x438c0+1, url, header
        );
        System.out.printf("0X%X\n", number.intValue());
        int hash = number.intValue();
        if (this.vm.getObject(hash) == null) {
            System.out.printf("0X%X is null\n", number.intValue());
        }
        UnidbgPointer p = memory.pointer(hash & 0xffffffffL);
        return p.getString(0);
    }

    public static void main(String[] args) {
        String s1 = "https://ichannel.snssdk.com/service/2/app_alert_check/?ac=wifi&channel=shenmasem_ls_dy_210&aid=1128&app_name=aweme&version_code=230300&version_name=23.3.0&device_platform=android&os=android&ssmix=a&device_type=Pixel&device_brand=google&language=zh&os_api=27&os_version=8.1.0&openudid=b104cd40fd2b3224&manifest_version_code=230301&resolution=1080*1794&dpi=420&update_version_code=23309900&_rticket=1670126182805&package=com.ss.android.ugc.aweme&cpu_support64=true&host_abi=armeabi-v7a&is_guest_mode=0&app_type=normal&minor_status=0&appTheme=light&need_personal_recommend=1&is_android_pad=0&ts=1670126133&cdid=26ed513b-3f69-440f-ba7d-4b53f333e88c&md=0&iid=4072246474186391&device_id=3122268427780248&uuid=352531081299354";
        String s2 = "x-ss-req-ticket\r\n"+
                "1646193928088\r\n"+
                "personal-recommend-status\r\n"+
                "1\r\n"+
                "x-vc-bdturing-sdk-version\r\n"+
                "2.2.1.cn\r\n"+
                "passport-sdk-version\r\n"+
                "30626\r\n"+
                "sdk-version\r\n"+
                "2\r\n"+
                "x-tt-trace-id\r\n"+
                "00-48cde91e0100ba02e9a49302ff57211e-48cde91e0100ba02-01\r\n"+
                "user-agent\r\n"+
                "com.ss.android.ugc.aweme/230300 (Linux; U; Android 8.1.0; zh_CN; Pixel; Build/OPM1.171019.014;tt-ok/3.12.13.1)\r\n"+
                "accept-encoding\r\n"+
                "gzip, deflate";
        Sign6 sign6 = new Sign6();
        String sign = sign6.GetSign(s1, s2);
        System.out.println(sign);
    }
}
