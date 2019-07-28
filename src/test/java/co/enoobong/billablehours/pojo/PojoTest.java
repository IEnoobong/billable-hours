package co.enoobong.billablehours.pojo;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.filters.FilterChain;
import com.openpojo.reflection.filters.FilterPackageInfo;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.EqualsAndHashCodeMatchRule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class PojoTest {
  private static final String[] POJO_PACKAGES =
          new String[]{
                  "co.enoobong.billablehours.backend.data",
          };

  private List<PojoClass> getPojoClasses() {
    final List<PojoClass> pojoClasses = new ArrayList<>();
    for (String pojoPackage : POJO_PACKAGES) {
      final List<PojoClass> pojoClassesRecursively =
              PojoClassFactory.getPojoClassesRecursively(
                      pojoPackage, new FilterChain(new FilterPackageInfo()));
      pojoClasses.addAll(pojoClassesRecursively);
    }
    return pojoClasses;
  }

  @Test
  public void validateGettersAndSetters() {
    Validator validator =
            ValidatorBuilder.create()
                    .with(new SetterMustExistRule(), new GetterMustExistRule())
                    .with(new SetterTester(), new GetterTester())
                    .with(new NoStaticExceptFinalRule())
                    .with(new EqualsAndHashCodeMatchRule())
                    .with(new NoFieldShadowingRule())
                    .with(new NoPublicFieldsExceptStaticFinalRule())
                    .build();

    validator.validate(getPojoClasses());
  }

}
