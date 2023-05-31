# L2ModularBlock

This library provides a new way of creating blocks: It allows modularization of block
methods using a decoration mechanics, and thus make the code base easier to maintain.

## Motivating Example
In Minecraft, there is the `EntityBlock` class, and there is the `HorizontalDirectionalBlock`.
However, block classes cannot extend both classes, making blocks such as the furnace blocks
need to extend one and re-implement one. This creates a lot of code redundancy, and
make project harder to maintain.

With modularization, we can think about it in another perspective: `EntityBlock` and
`HorizontalDirectionBlock` each provides one feature to the block: having an entity and
having horizontal directions. Each feature requires multiple method implementation.
Having block entity requires a block to have:
- BlockEntity ticker
- BlockEntity supplier
- Click handler for menu (optional)
- Placing information (such as copying title from stack)
- Analog output signal
- Removal listener for dropping inventories

Having horizontal directions requires a block to have:
- Add block state properties
- Rotate and Mirror
- Placement state

Thus, re-implementing any of the classes will cause multiple methods to be copied.

## Introduction
We use various `BlockMethod` to represents properties of a block that can be
overriden by subclasses. A block can be constructed with a collection of `BlockMethod`,
decorators, and each decorator may implement multiple `BlockMethod` interfaces.
For example, `BlockEntityBlockMethodImpl` and `HorizontalBlockMethodImpl` both implement
at least 3 `BlockMethod` interfaces each. By collecting the decorators, the block
will inherit the behavior from all decorators. 

Some `BlockMethod` interfaces allows only one decorator, 
and some allows multiple. The ones allowing only one decorator usually requires
the decorator to provide some output from the block, such as analog output, power output,
item representation of the block, shape, and light level,
and thus only one output is allowed. These `BlockMethod` all extends `SingletonBlockMethod`
interface and are located in `dev.xkmc.l2modularblock.one`.

Other `BlockMethod` interfaces allows multiple decorators. They usually execute some actions
when something happens to the block, or allows multiple outputs from decorators.
For example, collecting block state properties, click listener, place listener,
and removal listener. Noticeably, default state and placement state providers
are also allowing multiple decorators. They form a chain and allow each
decorators to modify the value. The order of invokation is the order they are provided
to the block on creation. These `BlockMethod` all extends `MultipleBlockMethod` interface and
are located in `dev.xkmc.l2modularblock.mult`.

## Usage

To create a block instance, we use `DelegateBlock.newBaseBlock`. The decorators
should be static final field of a class, to reduce instantiation if used in multiple blocks.

To create your new decorator, create a new class or record that implements one or multiple
`BlockMethod` interfaces. If you have multiple blocks that share something in common, you shold
divide your decorators into smaller modules for better re-usability.

### BlockEntity interfaces

The default `BlockEntityBlockMethodImpl` will add several optional features to the block
if the block entity class matches some requirements:
- If the block entity implements `MenuProvider`, right-clicking the block will open the menu.
- If the block entity implements `NameSetable`, placing the block will copy the name of the stack over.
- If the block entity implements `Container` or `BlockContainer`, destroying the block will drop contents inside.
- If the block entity implements `TickableBlockEntity`, the block will create a ticker for the block entity.
- If the block entity implements `Container` or have `ItemHandler` Capability, it will have analog signal output.

### Design Example
For example, if I'm making a cuisine mod with 5 blocks, where 3 of them can be lit through flint and steel,
3 of them have caps that can be opened and closed, 4 of them allow adding and taking items through right click,
and 2 of them have horizontal directions, while no 2 blocks have exact same set of functions.

I can create `LitFireImpl` that adds a lit block state property and listen to block click,
`OpenCapImpl` and `CloseCapImpl` that adds an open-cap block state property and listen to block click, 
`TakeItemImpl` and `InsertItemImpl` that listens to block click, reuse the `HorizontalDirectionBlockMethodImpl`,
and create a `BlockEntityBlockMethodImpl` instance for each of them.

During construction, I can specify the order of the decorators so that operations can be performed
with the right priority. For example, items can be added only if the cap is open, 
and when there are items available to take player should be able to take it before adding 
new items, so the order should be `LitFireImpl`, `OpenCapImpl`, `TakeItemImpl`, `InsertItemImpl`, `CloseItemImpl`.
This is an example of how to accommodate complex interaction logic without writing huge chunks of if statements using
decorators.

## Add more BlockMethod

I write the library as I go, so there could be a lot of missing block methods. You can always
add new block methods (and mark the implementation method in `DelegateBlockImpl` as final) and
create a new version.