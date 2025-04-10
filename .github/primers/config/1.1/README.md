# Config Format v1.1

To make it less confusing for users to configure custom generator, I decided to merge `customGen` with basic generators (`cobbleGen`/`stoneGen`/`basaltGen`). This format is a partial port of [GH-53](https://github.com/null2264/CobbleGen/issues/53).

> [!NOTE]
> If you still want to use format v1.0, you can set `formatVersion` to `1.0` (do note that it'll be removed completely at some point):
>
> ```json5
> {
>     "formatVersion": "1.0",
>     "cobbleGen": [
>         ...
>     ],
>     "customGen": {
>         ...
>     }
> }
> ```

## v1.0

Previously in v1.0, you'd define custom generators inside `customGen`, but most people got confused about the `cobbleGen`/`stoneGen`/`basaltGen` and thought it's a name for the custom generator.

```json5
{
    "cobbleGen": [
        {
            "id": "minecraft:cobblestone",
            "weight": 100.0
        }
    ],
    "customGen": {
        // This is the most confusing part for most people
        "cobbleGen": {
            "minecraft:bedrock": [
                {
                    "id": "#minecraft:ores",
                    "weight": 100.0
                }
            ]
        }
    }
}
```

## v1.1

Now in v1.1, all you need to do to define a custom generator is by adding `modifier` value inside the "result block", similar to how you set the generation's `weight`.

```json5
{
    "cobbleGen": [
        {
            "id": "minecraft:cobblestone",
            "weight": 100.0
        },
        {
            "id": "#minecraft:ores",
            "weight": 100.0,
            "modifier": "minecraft:bedrock"
        }
    ]
}
```
