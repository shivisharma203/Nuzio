# Contributing to Nuzio News App

Thank you for your interest in contributing!

## How to Contribute

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes**
4. **Write or update tests**
5. **Commit your changes**: `git commit -m 'feat: add amazing feature'`
6. **Push to the branch**: `git push origin feature/amazing-feature`
7. **Open a Pull Request**

## Development Guidelines

### Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions small and focused
- Write self-documenting code

### Commit Message Format
Follow [Conventional Commits](https://www.conventionalcommits.org/):
```
type(scope): subject

body

footer
```

**Types**: feat, fix, docs, style, refactor, test, chore

**Example**:
```
feat(auth): add Google Sign-In support

Implemented Google Sign-In using Firebase Authentication
with proper error handling and loading states.

Closes #123
```

### Testing Requirements
- Add unit tests for new business logic
- Add instrumentation tests for UI changes
- Ensure all tests pass: `./gradlew test`
- Maintain test coverage above 80%

### Pull Request Process
1. Update README.md if adding new features
2. Update documentation for API changes
3. Add tests for new functionality
4. Ensure CI/CD pipeline passes
5. Request review from maintainers

## Reporting Bugs

Use the GitHub Issues tab with the bug template:
- Clear description of the issue
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Device information and Android version

## Feature Requests

Submit feature requests via GitHub Issues:
- Clear description of the feature
- Use case and motivation
- Proposed implementation (optional)

## Questions?

Open an issue with the "question" label or reach out via:
- GitHub Discussions
- Email: shivisharma203@gmail.com.com

## Code of Conduct

Be respectful and inclusive. Harassment and discrimination will not be tolerated.