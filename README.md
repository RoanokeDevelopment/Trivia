<h1>Trivia - A Fabric 1.19.2 Question & Reward Plugin</h1>

<h3>Created for the Roanoke Cobblemon Server to make Cobblemon items more obtainable</h2>

On first run, the plugin will generate a Trivia folder with both questions & rewards JSON files.

Questions/Rewards are added under "pools", which could be treated as categories or "difficulty ranges".

![img.png](img.png)

<h2>Commands</h2>
<li><b>/quizreload [trivia.quizreload]</b> - reload questions & rewards files</li>
<li><b>/quizinterval (seconds) [trivia.quizinterval]</b> - set the amount of time that should pass between questions</li>
<li><b>/quizstart [trivia.quizstart]</b> - force start a quiz, useful for testing questions/rewards</li>

<h2>Questions & Rewards Files</h2>

On first run, these should be generated automatically, you can change them under /config/Trivia/

Both questions & rewards are under "pools", think of these as categories or difficulty types. Questions under the "easy" pool will give rewards from the "easy" pool, but the same could be done for Pokemon vs Minecraft trivia.

