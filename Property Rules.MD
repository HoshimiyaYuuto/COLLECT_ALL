# 主角屬性規則 Property Rule for Main Character


- **光哥 Bald  	    屬性: 水 	Property: Water**
	1. 生成方塊時，會熄滅生成有效區域之所有火堆，生成水方塊
	2. 生成方塊時，若生成有效區域有長草，則有50%機率使連續草堆往相鄰之隨機空區域多生長一格，但若無空區域則不生長，而水方塊僅生成到目標方向至首個連續草堆的前一格區域(新生成之最後一個水方塊與該連續草堆間有一格空區域)
	
- **火燄忍 Flameman  屬性: 火		Property: Fire**
	1. 生成方塊時，會燒毀生成有效區域之所有連續草堆(只要連續草堆有與生成有效區域重疊到即符合)，生成火堆
	2. 生成方塊時，若生成有效區域有水或冰方塊，則火堆僅生成到目標方向至首個水或冰方塊的前一格區域(新生成之最後一個火堆與該水或冰方塊間有一格空區域)，且原本冰方塊變成水方塊
    3. 生成方塊時，若生成有效區域內有食物存在，則食物位置不會產生火堆
	
- **冰結忍 Frozenman 屬性: 冰		Property: Ice**
	1. 生成方塊時，會熄滅生成有效區域之所有火堆，並在原火堆位置生成水方塊，而空區域仍生成冰方塊
	2. 生成方塊時，若生成有效區域已有水方塊，則使其變成冰方塊，但僅適用在生成有效區域之水方塊，相鄰而不再生成有效區域之水方塊不在此限

- **法巫 Mage       	屬性: 魔		Property: Soil**
	1. 靈球被摧毀時會炸毀任何其周圍四面八方的所有方塊(包含地圖背景方塊，但無法傷害到怪獸)
	2. 生成方塊時，僅目標方向之第一格(若該格為空區域)會生成魔方塊
    3. 若靈球爆烈範圍有其他靈球，會一同引發爆裂鏈

- **機鉨鈦鎂 Robot	屬性: 金		Property: Metal**
	1. 無屬性相剋規則

- **信長 Samurai		屬性: 草		Property: Wood**
	1. 生成方塊時，會吸收生成有效區域之所有水方塊，生成連續草堆

## 名詞解釋
- 邊界方塊: 地圖上的背景方塊與和該角色屬性規則無關或相同屬性之屬性方塊(ex: 若為光哥，該規範之屬性方塊有水,冰,魔,金方塊，若為冰結忍，該規範之屬性方塊有冰,魔,金,木，依此類推)
- 生成有效區域: 角色往目標方向望去，其與邊界方塊之間的地圖區域，而法巫之生成有效區域僅目標方向之第一格(若該格為空區域)
- 連續草堆: 在地圖上有與其他草方塊或草堆相鄰的草堆幾何區域集合，若僅有單個草方塊亦符合此定義
- 空區域: 當下無任何方塊存在的地圖區域

## 備註
- 角色摧毀方塊時，必須與方塊相鄰，一次摧毀符合位於目標方向且有相鄰且為該角色屬性之方塊(ex: 光哥只能摧毀水方塊，法巫只能摧毀魔方塊，依此類推)
- 怪獸當下的位置亦對生成方塊視為一種邊界方塊
- 地圖的各屬性方塊一段時間會與周圍能發生屬性互剋狀態的其他屬性方塊發生反應
- 火堆與靈球方塊與食物位置不會重疊，其他種技能方塊則可




# Property Rule for Main Character


- **Bald  	        Property: Water**
	1. When generating blocks, all fire pits within the Effective Generation Zone will be extinguished and replaced with Water Blocks.
	2. When generating blocks, if there is grass within the Effective Generation Zone, there is a 50% chance that the contiguous grass patch will expand by one random adjacent empty tile. However, if no empty tile is available, it will not grow. In this case, Water Blocks will only generate from the target direction up to the tile right before the first contiguous grass patch (leaving exactly one empty tile between the last newly generated Water Block and said contiguous grass patch).

- **Flameman		Property: Fire**
	1. When generating blocks, all contiguous grass patches overlapping with the Effective Generation Zone will be incinerated and replaced with Fire Pits.
	2. When generating blocks, if there are Water or Ice Blocks within the Effective Generation Zone, Fire Pits will only generate from the target direction up to the tile right before the first Water or Ice Block (leaving exactly one empty tile between the last newly generated Fire Pit and said Water/Ice Block). Concurrently, the initial Ice Block will melt and transform into a Water Block.
	3. When generating blocks, if food exists within the Effective Generation Zone, no Fire Pit will be generated on that specific food tile.

- **Frozenman		Property: Ice**
	1. When generating blocks, all fire pits within the Effective Generation Zone will be extinguished and replaced with Water Blocks, while any empty tiles will still generate Ice Blocks.
	2. When generating blocks, if there are existing Water Blocks within the Effective Generation Zone, they will freeze into Ice Blocks. Note that this only applies to Water Blocks directly within the Effective Generation Zone; adjacent Water Blocks outside this zone will remain unaffected.

- **Mage     		Property: Magic**
	1. When the Mana Orb is destroyed, it triggers an explosion that obliterates all blocks in all directions within its radius (including map background blocks, though it cannot damage monsters).
	2. When generating blocks, a Magic Block will only be generated on the first tile in the target direction (provided that the tile is an empty tile).
	3. If another Mana Orb is within the explosion radius of a detonated Mana Orb, it will trigger a chain explosion.

- **Robot	        Property: Metal**
	1. Immune to all property counter-mechanics (No property counter-rules apply).

- **Samurai		    Property: Wood**
	1. When generating blocks, it will absorb all Water Blocks within the Effective Generation Zone to generate Contiguous Grass Patches.

## Terminology
- Boundary Blocks: Refers to map background blocks, as well as property blocks that are either identical to or unrelated to the character's active property counter-rules (e.g., for Bald, these include Water, Ice, Magic, and Metal blocks; for Frozenman, these include Ice, Magic, Metal, and Wood blocks, and so forth).
- Effective Generation Zone: The linear map area stretching from the character's line of sight toward the target direction up to the first encountered Boundary Block. Specifically for Mage, the Effective Generation Zone is strictly limited to the first tile in the target direction (and only if it is an empty tile).
- Contiguous Grass Patches: A geometric cluster of grass patches on the map that are adjacent to other grass blocks or patches. A single isolated grass block also satisfies this definition.
- Empty Tile: A map location where no blocks currently exist.

## Notes
- To destroy blocks, a character must be adjacent to them. A single destruction command will eliminate all adjacent blocks in the target direction that match the character's specific property (e.g., Bald can only destroy Water Blocks, Mage can only destroy Magic Blocks, and so forth).
- The current coordinate of a monster is treated as a Boundary Block for block generation.
- Property blocks on the map will react with surrounding blocks of opposing properties after a certain duration based on property counter-mechanics.
- Fire Pits, Mana Orbs, and Food items cannot overlap with one another; however, other types of skill blocks are permitted to overlap.




# 主人公の属性規則


- **ハゲ兄  	        属性：水**
	1. ブロック生成時、エフェクト生成有効領域内にあるすべての火の粉を消火し、水ブロックを生成する
	2. ブロック生成時、エフェクト生成有効領域内に長草がある場合、50%の確率で連続草群が隣接するランダムな空領域に1マス増殖する。ただし、隣接する空領域がない場合は増殖しない。また、水ブロックは対象方向から最初の連続草群の手前1マスまでしか生成されない（新しく生成された最後の水ブロックと、該当する連続草群との間に1マスの空領域が残る）

- **フレイムマン		属性：火**
	1. ブロック生成時、エフェクト生成有効領域と重なるすべての連続草群を焼き尽くし、火の粉を生成する
	2. ブロック生成時、エフェクト生成有効領域内に水または氷ブロックが存在する場合、火の粉は対象方向から最初の水・氷ブロックの手前1マスまでしか生成されない（新しく生成された最後の火の粉と、該当する水・氷ブロックとの間に1マスの空領域が残る）。この時、既存の氷ブロックは融解して水ブロックに変化する
	3. ブロック生成時、エフェクト生成有効領域内に「エサ（食物）」が存在する場合、そのエサの位置には火の粉は生成されない

- **フリーズマン		属性：氷**
	1. ブロック生成時、エフェクト生成有効領域内のすべての火の粉を消火し、その位置に水ブロックを生成する。なお、もともと空領域だった場所には通常通り氷ブロックが生成される
	2. ブロック生成時、エフェクト生成有効領域内にすでに水ブロックが存在する場合、それを氷ブロックに変化させる。ただし、これはエフェクト生成有効領域内の水ブロックにのみ適用され、領域外に隣接する水ブロックは変化しない

- **ウィザード     	属性：魔**
	1. 魔導霊球が破壊された時、その周囲四面八方すべてのブロック（マップ背景ブロックを含む）を爆破する（ただし、モンスターにダメージを与えることはできない）
	2. ブロック生成時、対象方向の「最初の1マス」のみ（そのマスが空領域の場合）に魔ブロックを生成する
	3. 霊球の爆発範囲内に他の霊球が存在する場合、それらも誘爆し、連鎖爆発（チェイン）を引き起こす

- **ロボッ	        属性：金**
	1. 属性相克（属性間の相性ルール）を一切無視する

- **信長		    　　属性：草**
	1. ブロック生成時、エフェクト生成有効領域内にあるすべての水ブロックを吸収し、連続草群を生成する

## 用語解説
- 絶対境界ブロック: マップ上の背景ブロック、および該当キャラクターの属性ルールにおいて「無関係」または「同属性」である属性ブロック（例：ハゲ兄の場合、対象外となる水、氷、魔、金ブロック。フリーズマンの場合、氷、魔、金、木ブロック。以下同様）
- エフェクト生成有効領域: キャラクターが対象方向を見つめた際、自身と「絶対境界ブロック」との間にあるマップ領域。ただし、ウィザードの生成有効領域は、対象方向の最初の1マス（空領域の場合のみ）に限定される
- 連続草群: マップ上で他の草ブロックまたは草群と隣接している、草の幾何学的な集合領域。単一の草ブロックのみが存在する場合もこの定義を満たす。
- 空領域: 現時点でブロックが一切存在しないマップ上の領域。
## 備考
- キャラクターがブロックを破壊する際、そのブロックに隣接している必要がある。1回の破壊アクションで、対象方向にあり、かつ隣接している「自身の属性と一致するブロック」を破壊できる（例：光哥は水ブロックのみ、法巫は魔ブロックのみを破壊可能。以下同様）。
- モンスターの現在位置も、ブロック生成においては一種の「絶対境界ブロック」として扱われる。
- マップ上の各属性ブロックは、一定時間が経過すると、周囲にある相性の悪い（属性互克の関係にある）他の属性ブロックと反応を起こす。
- 火の粉、魔導霊球、エサ（食物）の位置が互いに重なり合うことはない。その他のスキルブロックは重ねて生成することが可能