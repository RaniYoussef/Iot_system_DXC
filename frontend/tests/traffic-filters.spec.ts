import { test, expect } from '@playwright/test';

test('TC018 - Filter traffic data by location, congestion, and date', async ({ page }) => {
  // 1. Go directly to traffic dashboard
  await page.goto('http://localhost:4200/traffic-dashboard');

  // 2. Wait for toggle-collapse link to appear
  const toggle = page.locator('a.toggle-collapse');
  await toggle.waitFor({ state: 'visible', timeout: 10000 });

  // 3. Expand filters if they are collapsed
  const toggleText = await toggle.innerText();
  if (toggleText.trim() === 'Expand') {
    await toggle.click();
  }

  // 4. Wait for location filter to be available
  const locationSelect = page.locator('#location-select');
  await locationSelect.waitFor({ state: 'visible', timeout: 10000 });

  // 5. Fill out filter fields
  await page.selectOption('#location-select', { label: 'Smouha' });
  await page.selectOption('#congestion-select', { label: 'High' });
  await page.fill('#from-date', '2025-05-01');
  await page.fill('#to-date', '2025-06-05');
  await page.selectOption('#sort-by-select', { value: 'trafficDensity' });
  await page.selectOption('#sort-direction-select', { value: 'desc' });

  // 6. Click Apply Filters
  await page.click('#apply-filters-button');

  // 7. Wait for table rows to appear
  await page.waitForSelector('table tbody tr', { timeout: 10000 });

  // 8. Assert that at least one result row is shown
  const rows = page.locator('table tbody tr');
  const count = await rows.count();
  expect(count).toBeGreaterThan(0);
});
