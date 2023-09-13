
`https://my.oschina.net/u/4030990/blog/3211858` 
`https://www.jianshu.com/p/68fcbc154c2f` 
 
```
package com.ofllibnnb.pap;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

@SupportedAnnotationTypes({ "com.pap.MyProcessor" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ProcessTest extends AbstractProcessor {

	JavacTrees trees;
	Context context;
	TreeMaker maker;
	// com.sun.tools.javac.tree.TreeMaker
	Names names;
	Messager messager;
	JavacElements elements;

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> procs = roundEnv.getElementsAnnotatedWith(MyProcessor.class);
		procs.forEach(ele -> {
			JCTree it = trees.getTree(ele);
			// class qualified name
			final String target = ((TypeElement) ele).getQualifiedName().toString();
			it.accept(new TreeTranslator() {

				@Override
				public void visitMethodDef(JCMethodDecl jc) {
					String methodName = jc.getName().toString();
					if (!"<init>".equals(methodName)) {

						List<JCVariableDecl> params = jc.params;
						List<JCExpression> ps = List.nil();
						List<JCExpression> as = List.nil();
						for (JCVariableDecl param : params) {
							ps = ps.append(maker.Literal(param.getType().type.toString()));
							as = as.append(maker.Ident(param));
						}

						// Caller.call("target", "method", new String[] { paramTypes }, new Object[] { args });
						JCMethodInvocation apply = maker.Apply(
								List.nil(),
								maker.Select(ma("com.ofllibnnb.pap.Caller"), names.fromString("call")),
								List.of(maker.Literal(target), maker.Literal(methodName),
										maker.NewArray(ma("java.lang.String"), List.nil(), ps),
										maker.NewArray(ma("java.lang.Object"), List.nil(), as)));

						// CallResult __$_PAP_CALL_RES__ = Caller.call(...);
						JCVariableDecl called = maker.VarDef(
								maker.Modifiers(0), 
								names.fromString("__$_PAP_CALL_RES__"),
								ma("com.ofllibnnb.pap.CallResult"), 
								apply);

						// return type, void, java.lang.String
						String rest = jc.getReturnType().type.toString();

						// call.isEnabled()
						JCMethodInvocation isEnabled = maker.Apply(
								List.nil(),
								maker.Select(maker.Ident(names.fromString("__$_PAP_CALL_RES__")), names.fromString("isEnabled")),
								List.nil());

						// call.getValue()
						JCMethodInvocation value = maker.Apply(
								List.nil(),
								maker.Select(maker.Ident(names.fromString("__$_PAP_CALL_RES__")), names.fromString("getValue")),
								List.nil());

						JCExpression ret = null;
						if (!"void".equals(rest)) {
							ret = maker.TypeCast(ma(rest), value);
						}

						// if (call.isEnabled()) return .;
						JCIf if1 = maker.If(isEnabled, maker.Return(ret), null);
						List<JCStatement> stats = jc.getBody().getStatements();

						List<JCStatement> s = List.nil();
						s = s.append(called);
						s = s.append(if1);
						for (JCStatement stat : stats) {
							s = s.append(stat);
						}
						jc.getBody().stats = s;
					}
					super.visitMethodDef(jc);
				}

			});
		});
		return false;
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		trees = JavacTrees.instance(processingEnv);
		context = ((JavacProcessingEnvironment) processingEnv).getContext();
		maker = TreeMaker.instance(context);
		names = Names.instance(context);
		messager = processingEnv.getMessager();
		elements = JavacElements.instance(context);

	}

	private JCTree.JCExpression ma(String components) {
		String[] componentArray = components.split("\\.");
		JCTree.JCExpression expr = maker.Ident(names.fromString(componentArray[0]));
		for (int i = 1; i < componentArray.length; i++) {
			expr = maker.Select(expr, names.fromString(componentArray[i]));
		}
		return expr;
	}

}

```