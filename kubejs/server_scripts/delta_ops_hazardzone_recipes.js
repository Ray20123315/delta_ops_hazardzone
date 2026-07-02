ServerEvents.recipes(event => {
  event.shapeless('delta_ops_hazardzone:bandage', [
    'minecraft:paper',
    '#minecraft:wool'
  ])

  event.shaped('delta_ops_hazardzone:painkiller', [
    ' S ',
    ' H ',
    '   '
  ], {
    S: 'minecraft:sugar',
    H: 'minecraft:honey_bottle'
  })

  event.shaped('delta_ops_hazardzone:medkit', [
    'BWB',
    'RHR',
    'BWB'
  ], {
    B: 'delta_ops_hazardzone:bandage',
    W: 'minecraft:glistering_melon_slice',
    R: 'minecraft:redstone',
    H: 'minecraft:honey_bottle'
  })

  event.shaped('delta_ops_hazardzone:surgery_kit', [
    'ISI',
    'PCP',
    'IRI'
  ], {
    I: 'minecraft:iron_ingot',
    S: 'minecraft:shears',
    P: 'minecraft:paper',
    C: 'minecraft:compass',
    R: 'minecraft:redstone'
  })
})
