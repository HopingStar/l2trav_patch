# L2 & Trav Patch (`l2trav_patch`)

> 修复 Minecraft 整合包中 **l2weaponry** 与 **traveloptics** 崩溃冲突的轻量补丁模组。
> A lightweight patch mod that fixes crash/conflict bugs between **l2weaponry** and **traveloptics** in modpacks.

- **Mod ID**: `l2trav_patch`
- **平台 / 游戏版本**: NeoForge / Minecraft **1.21.1**
- **作者**: [HopingStar](https://hopingflavor.com)
- **版本**: `1.0.2`

---

## 简介 / Overview

在整合包中同时安装 **l2weaponry** 与 **traveloptics** 时，会遇到两类互不相关的崩溃 / 报错：

1. l2weaponry 在启动时因 IceAndFireCE 兼容代码崩溃；
2. traveloptics 在注册战利品修饰器时抛 `Adding duplicate value` 错误。

本模组通过运行时 **Mixin** 对目标模组做最小侵入修复，游戏内自动生效，无需任何配置。

---

## 修复内容 / Fixes

### ① l2weaponry ↔ IceAndFireCE 崩溃（`CompatDispatchMixin`）

- **报错**: `No valid IaF target` / `DragonCompat` 静态初始化崩溃（缺少 `IafToolMaterials` 类）
- **原因**: l2weaponry `CompatDispatch.register()` 中执行 `if (ModList.isLoaded("iceandfire")) new DragonCompat()`，在 IceAndFireCE 安装时会触发 `DragonCompat` 静态初始化，而相关类不存在导致崩溃。
- **修复**: `@Redirect` 拦截 `ModList.isLoaded(...)`，对 `"iceandfire"` 恒返回 `false`，跳过冰火联动，其余模组判定不受影响。

### ② traveloptics 战利品重复注册（`TOLootModifiersMixin`）

- **报错**: `Adding duplicate value`（`universal_loot` 重复注册）
- **原因**: traveloptics `TOLootModifiers.<clinit>` 中 `"universal_loot"` 误用了 `KeyLootModifier.CODEC`（应为 `UniversalLootModifier.CODEC`），导致同一 CODEC 实例被注册两次。
- **修复**: `@ModifyArg` 把第二次 `DeferredRegister.register` 调用的 `Supplier` 参数改回 `UniversalLootModifier.CODEC`。

---

## 环境要求 / Requirements

| 项目 | 要求 |
| --- | --- |
| Minecraft | `1.21.1` |
| NeoForge | `21.1.215` 及以上（`[21.1.215,)`） |
| Java | **21** |
| 目标模组 | `l2weaponry`（可选依赖）、`traveloptics`（可选依赖） |

> 目标模组声明为 `optional` 依赖：只安装其中任意一个（甚至都不装）也能正常加载，但安装对应模组时才生效。

---

## 安装 / Installation

1. 下载 `l2trav_patch-1.0.2.jar`；
2. 放入 Minecraft 的 `mods` 文件夹；
3. 启动游戏，修复自动生效。

---

## 从源码构建 / Building from Source

```bash
./gradlew build
```

构建产物输出到 `build/libs/l2trav_patch-<version>.jar`。

**注意（构建前请留意）：**

- 本工程以 `compileOnly fileTree(...)` 方式引用本地 `mods` 文件夹里的目标模组 jar（`l2weaponry` / `traveloptics`）作为编译期类定义来源。在其他机器上构建前，请先修改 `build.gradle` 中的 `fileTree(dir: ...)` 路径，使其指向本机存放这两个模组的目录。
- `gradle.properties` 中保留了本机 Clash 代理配置（`127.0.0.1:7892`）。如果本机没有代理，或端口不同，请删掉 / 修改 `org.gradle.jvmargs` 与 `systemProp.*` 相关行，否则依赖下载可能失败。

### 版本号约定

每次改动后需三处同步升版本号：

1. `gradle.properties` → `mod_version`
2. `src/main/templates/META-INF/neoforge.mods.toml`（经 `generateModMetadata` 展开，无需手动改）
3. 产物 jar 文件名（由 `mod_version` 自动决定）

---

## 项目结构 / Project Structure

```
l2trav_patch/
├── build.gradle                  # Gradle 构建脚本（ModDevGradle 2.0.116）
├── gradle.properties             # 版本 / 模组元数据 / 网络代理配置
├── settings.gradle
├── src/main/
│   ├── java/com/l2trav/patch/
│   │   ├── PatchMod.java         # @Mod 入口
│   │   └── mixin/
│   │       ├── CompatDispatchMixin.java   # 修复 l2weaponry ↔ IceAndFireCE
│   │       └── TOLootModifiersMixin.java  # 修复 traveloptics 重复注册
│   ├── resources/
│   │   ├── l2trav_patch.mixins.json
│   │   └── l2trav_patch.png      # 模组图标
│   └── templates/META-INF/
│       └── neoforge.mods.toml    # 模组元数据模板（${...} 占位符展开）
└── .gitignore
```

---

## 许可证 / License

本模组以 **MIT License** 开源，详见 [LICENSE](./LICENSE)。

---

## 作者 / Author

- **HopingStar**
- 个人网站: <https://hopingflavor.com>
