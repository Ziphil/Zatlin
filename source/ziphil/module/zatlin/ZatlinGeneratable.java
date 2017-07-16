package ziphil.module.zatlin;

import java.util.List;


public interface ZatlinGeneratable {

  public String generate(ZatlinRoot root);

  // ルートに存在していない識別子を含んでいればそれを返し、そうでなければ null を返します。
  public ZatlinToken findUnknownIdentifier(ZatlinRoot root);

  // 中身を全て展開したときに identifiers に含まれる識別子トークンが含まれていればそれを返し、そうでなければ null を返します。
  // 識別子の定義に循環参照がないかを調べるのに用いられます。
  public ZatlinToken findCircularIdentifier(List<ZatlinToken> identifiers, ZatlinRoot root);

}