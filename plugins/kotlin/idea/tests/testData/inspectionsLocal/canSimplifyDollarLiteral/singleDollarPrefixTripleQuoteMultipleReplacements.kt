// COMPILER_ARGUMENTS: -Xmulti-dollar-interpolation
// IGNORE_K1

fun test() {
    $"""
        \$\$\$
        Foo${'$'}Bar
        ${'$'}${'$'}${'$'}
        ${'$'}${'$'}`Baz Boo`
        ${'$'}${'$'}{}
        ${'$'}${'$'}_Goo${'$'}${'$'}
        ${'$'}${'$'} Foo
        ${'$'}${'$'}${'$'}
<caret>${'$'}${'$'}"""
}