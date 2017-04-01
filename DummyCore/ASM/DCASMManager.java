package DummyCore.ASM;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import DummyCore.Utils.ASMManager;
import DummyCore.Utils.LoadingUtils;
import DummyCore.Utils.Notifier;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * 
 * @author modbder
 * @Description
 * Internal. Enables features like IItemOverlayElement
 */
public class DCASMManager implements IClassTransformer {

	public DCASMManager() {
		try{
			Class.forName("DummyCore.Utils.ASMManager");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		name = transformedName;
		if(ASMManager.strictCompareByEnvironment(name, "net.minecraft.client.renderer.RenderItem", "net.minecraft.client.renderer.RenderItem"))
			return handleItemOverlays(name,basicClass);
		//If the class we are loading exists
		if(basicClass != null) {
			//Creating a most basic bytecode->runtime command helper.
			ClassNode classNode = new ClassNode();
			//Parsing the bytecode to runtime commands
			ClassReader classReader = new ClassReader(basicClass);
			//Giving our helper a parsed list of commands without any code modifications.
			classReader.accept(classNode, 0);
			//If class requires inspection we are sending it into our method
			if(ASMManager.checkAnnotationForClass(classNode, "LDummyCore/Utils/DCASMCheck;"))
				return handleClass(name,transformedName,basicClass,classNode,classReader);
		}
		return basicClass;
	}

	public byte[] handleItemOverlays(String name, byte[] basicClass) {
		Notifier.notifyCustomMod("DCASM", "Transforming "+name);
		Notifier.notifyCustomMod("DCASM", "Initial byte[] count: "+basicClass.length);
		byte[] basic = basicClass.clone();
		try {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			
			MethodNode mn = ASMManager.getMethod(classNode, "renderItemOverlayIntoGUI", "func_180453_a!&!a", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V!&!(Lbdl;Ladz;IILjava/lang/String;)V");
			
			InsnList lst = new InsnList();
			lst.add(new LabelNode());
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new VarInsnNode(Opcodes.ILOAD, 3));
			lst.add(new VarInsnNode(Opcodes.ILOAD, 4));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 5));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummyCore/Utils/DummyHooks", "renderItemOverlayIntoGUI", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", false));
			
			mn.instructions.insert(mn.instructions.get(1), lst);
			
			classNode.accept(cw);
			byte[] bArray = cw.toByteArray();
			
			Notifier.notifyCustomMod("DCASM", "Finished Transforming "+name);
			Notifier.notifyCustomMod("DCASM", "Final byte[] count: "+bArray.length);
			
			return bArray;
		}
		catch(Exception e) {
			e.printStackTrace();
			LoadingUtils.makeACrash("[DCASM]Fatal errors occured patching "+name+"! This modification is marked as OPTIONAL, thus the loading can continue normally.", e, false);
			return basic;
		}
	}

	/**
	 * My dumb version of {@link net.minecraftforge.fml.common.Optional}
	 */
	public byte[] handleClass(String name, String transformedName,byte[] basicClass,ClassNode cn, ClassReader cr) {
		Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has requested a DummyCore ASM check via DummyCore/Utils/DCASMCheck annotation. Examining...");
		//Initializing the interfaces variable
		String[] checkedClss = new String[0];
		//Checking through all annotations.
		for(int i = 0; i < cn.invisibleAnnotations.size(); ++i) {
			AnnotationNode node = cn.invisibleAnnotations.get(i);
			//Checking if the annotation found is the one, that makes us go through 
			if(node.desc.equalsIgnoreCase("LDummyCore/Utils/ExistenceCheck;") && node.values != null && node.values.size() > 0) {
				Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has requested a DummyCore ASM check on it's implementations via DummyCore/Utils/ExistenceCheck annotation. Examining...");
				//Getting a full list of classes that we need to check for existence
				List<?> classes = List.class.cast(node.values.get(1)); 
				checkedClss = new String[classes.size()];
				checkedClss = String[].class.cast(classes.toArray(checkedClss));
				break;
			}
		}
		Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has given the next interfaces to check: "+Arrays.asList(checkedClss));
		//Creating the ability to modify bytecode using modified instructions
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		//If we have interfaces to check
		if(checkedClss.length > 0) {
			for(int i = 0; i < checkedClss.length; ++i) {
				//If the class was NOT found
				if(!classExists(checkedClss[i])) {
					//Looping through all interfaces presented in the class
					J:for(int j = 0; j < cn.interfaces.size(); ++j) {
						//If this is the one we are looking for
						if(cn.interfaces.get(j).equalsIgnoreCase(checkedClss[i].replace('.', '/'))) {
							Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has a "+cn.interfaces.get(j)+" implementation, but the referenced class was not found. Removing the given interface.");
							//Removing it.
							cn.interfaces.remove(j);
							break J;
						}
					}
				}
				else {
					Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has a "+checkedClss[i]+" implementation, and the referenced class was found. Skipping to the next interface...");
				}
			}
		}
		//Writing changed Instructions into bytecode helper
		cn.accept(cw);
		Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has been checked.");
		//Returning modified bytecode.
		return cw.toByteArray();
	}

	public boolean classExists(String s) {
		try {
			Class<?> c = Class.forName(s);
			return c != null;
		}
		catch(ClassNotFoundException e) {
			return false;
		}
	}
}
