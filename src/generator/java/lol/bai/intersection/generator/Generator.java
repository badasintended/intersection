package lol.bai.intersection.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureWriter;

public class Generator implements Opcodes {

    public static void main(String[] args) throws IOException {
        var outputDir = Path.of(args[0]);

        var packageName = "lol/bai/intersection";
        var packageDir = outputDir.resolve(packageName);
        Files.createDirectories(packageDir);

        try (var files = Files.walk(packageDir, 1)) {
            //noinspection ResultOfMethodCallIgnored
            files.map(Path::toFile).forEach(File::delete);
        }

        Files.createDirectories(packageDir);


        var objectType = Type.getType(Object.class);

        for (int i = 2; i <= 10; i++) {
            var shortClassName = "I" + i;
            var className = packageName + "/" + shortClassName;
            var classPath = outputDir.resolve(className + ".class");

            System.out.println("Generating " + shortClassName);

            String classSignature;
            {
                var sw = new SignatureWriter();

                for (int j = 1; j <= i; j++) {
                    sw.visitFormalTypeParameter("T" + j);

                    var bound = sw.visitClassBound();
                    bound.visitClassType(objectType.getInternalName());
                    bound.visitEnd();
                }

                sw.visitSuperclass();
                sw.visitClassType(objectType.getInternalName());
                sw.visitEnd();
                classSignature = sw.toString();
            }

            var cw = new ClassWriter(0);
            cw.visit(V1_8,
                ACC_PUBLIC | ACC_FINAL,
                className,
                classSignature,
                objectType.getInternalName(), null);
            cw.visitSource(shortClassName + ".java", null);
            cw.visitField(ACC_PRIVATE | ACC_FINAL,
                "object", objectType.getDescriptor(), null, null);

            {
                var mv = cw.visitMethod(ACC_PUBLIC,
                    "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, objectType),
                    null, null);

                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL,
                    objectType.getInternalName(),
                    "<init>", Type.getMethodDescriptor(Type.VOID_TYPE), false);

                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, className, "object", objectType.getDescriptor());

                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }

            {
                var sw = new SignatureWriter();
                sw.visitFormalTypeParameter("T");
                sw.visitTypeVariable("T1");


                for (int j = 2; j <= i; j++) {
                    var bound = sw.visitInterfaceBound();
                    bound.visitTypeVariable("T" + j);
                }

                sw.visitReturnType();
                sw.visitTypeVariable("T");
                sw.visitEnd();

                var mv = cw.visitMethod(
                    ACC_PUBLIC,
                    "get", Type.getMethodDescriptor(objectType),
                    sw.toString(), null);

                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, "object", objectType.getDescriptor());
                mv.visitInsn(ARETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }

            cw.visitEnd();
            Files.write(classPath, cw.toByteArray());
        }
    }

}
