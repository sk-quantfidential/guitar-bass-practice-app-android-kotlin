# Guitar Bass Practice App - TODO List

## High Priority Tasks

### Testing & Quality Assurance
- [ ] **Fix integration test runtime failures**
  - Fix `UIComponentTests.testOfflineModeIndicatorVisibility` - "Cannot call setContent twice per test!" error
  - Fix `UIComponentTests.testAccessibilitySettingsToggle` - Multiple "High Contrast" nodes found
  - Resolve other integration test failures that occur during `connectedDebugAndroidTest`
- [ ] **Add missing test coverage**
  - UI tests for nested scrollable error scenarios (currently placeholder tests)
  - Integration tests for offline capabilities
  - Performance tests for large exercise lists
  - End-to-end tests for exercise creation and playback

### Bug Fixes
- [ ] **Fix nested scrollable components** (Original issue)
  - Remove nested `LazyColumn` in `ExerciseCustomizationPanel.kt:37-42`
  - Remove nested `LazyColumn` in AI tab components
  - Implement single scrolling container pattern in `MainScreen.kt:301-312`
- [ ] **Address data model inconsistencies**
  - Verify `ExerciseProgress` percentage field type (Int vs Float)
  - Check `ExercisePlaybackState` API for missing volume/speed parameters
  - Validate `ExerciseEngine.handleEvent()` method signature changes

### Code Quality & Architecture
- [ ] **Refactor UI components to follow best practices**
  - Convert nested scrollable components to single scrolling containers
  - Implement proper Compose state management patterns
  - Add proper error boundaries and loading states
- [ ] **Database schema validation**
  - Ensure all entity relationships are properly defined
  - Add migration tests for database schema changes
  - Validate foreign key constraints

## Medium Priority Tasks

### Feature Enhancements
- [ ] **Improve exercise creation workflow**
  - Add validation for exercise parameters
  - Implement exercise preview functionality
  - Add exercise templates and presets
- [ ] **Enhance offline capabilities**
  - Implement offline exercise storage
  - Add sync mechanism for offline changes
  - Improve offline indicator UX
- [ ] **Accessibility improvements**
  - Fix multiple accessibility nodes issue
  - Add screen reader support for fretboard visualizer
  - Implement proper focus management
  - Add keyboard navigation support

### Performance Optimizations
- [ ] **Optimize UI rendering**
  - Implement lazy loading for exercise lists
  - Add image caching for fretboard diagrams
  - Optimize Compose recomposition performance
- [ ] **Database performance**
  - Add database indexes for common queries
  - Implement query result caching
  - Optimize large dataset handling

## Low Priority Tasks

### Documentation
- [ ] **Code documentation**
  - Add KDoc comments for public APIs
  - Document architecture decisions
  - Create component usage examples
- [ ] **User documentation**
  - Create user guide for exercise creation
  - Document accessibility features
  - Add troubleshooting guide

### Technical Debt
- [ ] **Code cleanup**
  - Remove unused imports and variables (current warnings)
  - Standardize error handling patterns
  - Refactor large components into smaller, focused components
- [ ] **Build system improvements**
  - Optimize build performance
  - Add static code analysis tools
  - Implement automated dependency updates

## Testing Strategy Notes
- **Unit Tests**: Currently 85 tests with 100% pass rate ✅
- **Integration Tests**: Now compile successfully, but have runtime failures
- **UI Tests**: Need real component testing instead of placeholder tests
- **E2E Tests**: Not yet implemented

## Architecture Notes
- Using Room database with proper DAOs
- Jetpack Compose for UI with Material3
- Hilt for dependency injection
- MVVM architecture pattern
- Domain-driven design with proper layer separation

---
*Last Updated: August 14, 2025*
*Test Status: Unit tests passing, Integration tests compiling but failing at runtime*