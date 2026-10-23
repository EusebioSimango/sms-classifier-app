# As classes JNI do ONNX Runtime e do SQLCipher são procuradas pelo código nativo
# por nome; se o R8 as renomear ou remover, a app falha ao carregar o modelo ou a base.
-keep class ai.onnxruntime.** { *; }
-keep class net.zetetic.database.** { *; }
-dontwarn ai.onnxruntime.**

# Anotações só de compilação usadas pelo Tink (security-crypto); não existem em runtime.
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
